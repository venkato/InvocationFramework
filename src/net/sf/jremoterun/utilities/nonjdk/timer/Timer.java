package net.sf.jremoterun.utilities.nonjdk.timer;

import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * This class can be used to schedule task in specific period.
 * <p>
 * Philosophy: All waiting activity must be in timer.
 *
 * TODO handle wakeup after hibernate of computer
 */
public class Timer {

	private final static WeakHashMap<Timer, Object> timers = new WeakHashMap();

	private static final long serialVersionUID = 3079923224890192228L;

	public static volatile boolean stopGlobal = false;

	private volatile long lastRunBefore = -1;

	public static long notImportantTimeDiffInMS = 30000;
	public static long timeDiffForHebirnateRecheck = 1000 * 60 * 5;

	// public static volatile boolean catchDebug = false;
	public static Runnable debugWaitNextRun;

	protected volatile transient TimerThread waitingThread;

	private transient static final Logger log = LogManager.getLogger();

	private Date startDate;

	public static final Object object = new Object();

	protected volatile long lastRun = -1;

	// private volatile boolean changeStop = false;

	protected volatile long nextFireTime;

	private boolean waitingThreadRunning = false;

	// public static boolean stopGlobal = false;

	protected volatile boolean stop = false;

	protected volatile boolean inSleep = false;

	protected boolean reevalute;

	private final TimerController controller;

	// private boolean timerRunning = false;

//	private final Object wakeUpLock = new Object();
	private final WaitNotifyMethods2 wakeUpLock = new WaitNotifyMethods2();

	// protected final Object lock = new Object();

	protected final Object lock2 = new Object();

	public Timer(final TimerController timerController) {
		timers.put(this, object);
		controller = timerController;
	}

	public static Set<Timer> getTimers() {
		return new HashSet<Timer>(timers.keySet());
	}

	public static void stopAllTimers() {
		for (final Timer timer : Timer.getTimers()) {
			// if (timer.isTimerRunning()) {
			timer.stop();
			// log.info(timer.reEvauateNextRun());
			// }
			timer.reEvauateNextRun();
		}

	}

	/**
	 * In the same thread: If stop() -> isTimerRunning()=false If stop() ->
	 * start() ok
	 */
	public void stop() {
		synchronized (lock2) {
			stop = true;
		}
	}


	private Date nextFireDateDebug;


	/**
	 * Only in debug purpose, as nextFireTime can be changed : <ul>
	 * <li>user may ask run earlier
	 * <li>computer can go to hibernate
	 * </ul>
	 */
	public Date getNextFireTimeDebug() {
		return nextFireDateDebug;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void taskWasRunNow() {
		// Assert.assertNotNull(waitingThread);
		setLastRunTime(System.currentTimeMillis());
		reEvauateNextRun();
	}

	public volatile Date debugLastTimeStartedWait;

	protected void waitNextRun() throws InterruptedException {
		Date nextFireDate2 = controller.isRunNow();
		if (nextFireDate2 != null) {
			nextFireTime = nextFireDate2.getTime();
		} else {
			nextFireDate2 = controller.getNextFireDate();
			if (nextFireDate2 == null) {
				stop = true;
				log.info("stopping");
			} else {
				final long nextFireDate3 = nextFireDate2.getTime();
				if (lastRun != -1) {
					if (nextFireDate3 - lastRun + 10 * 1000 < 0) {
						stop = true;
						log.info("stopping: incorrect time " + nextFireDate2);
						return;
					}
				}
				nextFireDateDebug = nextFireDate2;
				reevalute = false;
				try {
					// boolean doNewThisMethod = false;
					final long currcentTime;
					final long waitTime;
					synchronized (wakeUpLock.lock) {
						currcentTime = System.currentTimeMillis();
						waitTime = nextFireDate3 - currcentTime;
						inSleep = true;
						if (waitTime > 0) {
							// if (stopGlobal) {
							// throw new RuntimeException(getController()
							// .toString());
							// }
							if (debugWaitNextRun != null) {
								debugWaitNextRun.run();
							}
							debugLastTimeStartedWait = new Date();
							wakeUpLock.wait2(waitTime);
							// if (catchDebug) {
							// log.info(getController().toString());
							// }
							// log.info("Reevaluate =" + reevalute);
							if (reevalute) {
							} else {
								// Thread.sleep(waitTime);
								nextFireTime = nextFireDate3;
							}
						} else {
							nextFireTime = currcentTime;
						}
						inSleep = false;
					}
					if (waitTime > timeDiffForHebirnateRecheck) {
						long nowAfterWait = System.currentTimeMillis();
						if (Math.abs(nowAfterWait - nextFireDate3) > notImportantTimeDiffInMS) {
							// may be caused by hibernate event
							boolean needRun2 =  notifyMissingTimeEvent(nextFireDate2);
							if(needRun2){

							}else {
								waitNextRun();
								return;
							}
						}
					}
					if (reevalute) {
						final boolean b1 = isRunNowInternal();
						// log.info("isRunNowInternal=" + b1);
						if (b1) {
							waitNextRun();
							// doNewThisMethod = true;
						} else {
							nextFireTime = System.currentTimeMillis();
						}
					}
					// nextFireTime = DateCountDown.waitDate(nextFireDate3)
					// .getTime();

					// TODO may be use isHasLock(lock) method
					// System.err.println("normal exit");
				} finally {
					reevalute = false;
					// synchronized (lock) {
					// if (waitingThread != null) {
					// waitingThread.getContextClassLoader();
					// }
					// }
				}
			}
		}
	}

	/**
	 * If start() -> isTimerRunning()=true
	 */

	// send signal run task. No wait finishing. Return true is this method run
	// task.
	public boolean reEvauateNextRun() {
		synchronized (lock2) {
			if (waitingThread == null) {
				return false;
			}
			synchronized (wakeUpLock.lock) {
				if ((waitingThread != null) && inSleep) {
					reevalute = true;
					wakeUpLock.notify2();
					return true;
				}
			}
			return false;
		}
	}

	public boolean isInSleep() {
		return inSleep;
	}

	private boolean isRunNowInternal() {
		// changeStop = false;
		Assert.assertNotNull(waitingThread);
		return !stop;
	}

	public int getWaitingThreadPriotity() {
		if (waitingThread == null) {
			return -1;
		}
		return waitingThread.getPriority();
	}

	public void setWaitingThreadPriotity(final int newPriority) {
		waitingThread.setPriority(newPriority);
	}

	public void start() {
		synchronized (lock2) {
			inSleep = false;
			final Thread thread = waitingThread;
			if (stop && thread != null && thread.isAlive() && waitingThreadRunning) {
				log.info("reusing waiting thread");
				// we need to start waiting thread to avoid 2 waiting threads
				stop = false;
				// changeStop = true;
				reEvauateNextRun();
			} else {
				stop = false;
				startDate = new Date();
				nextFireTime = -1;
				waitingThread = new TimerThread();
				setThreadName(waitingThread, "Waiting thread");
				waitingThread.start();
				// log.info("starting");
			}
		}
	}

	public boolean isTimerRunning() {
		final Thread thread = waitingThread;
		return thread != null && thread.isAlive() && !stop;
	}

	public TimerController getController() {
		return controller;
	}

	public TimerThread getWaitingThread() {
		return waitingThread;
	}

	public Date getLastRun() {
		if (lastRun == -1) {
			return null;
		}
		return new Date(lastRun);
	}

	public static boolean runTaskIfMissesTime = true;

	protected boolean notifyMissingTimeEvent(Date date) {
		log.info("missing fire date: " + date);
		return runTaskIfMissesTime;
	}

	public void setLastRun(Date lastRun) {
		if (lastRun == null) {
			this.lastRun = -1;
		} else {
			setLastRunTime(lastRun.getTime());
		}
	}

	protected class TimerThread extends Thread {

		@Override
		public void run() {
			waitingThreadRunning = true;
			try {
				Timer.setThreadName(this, "Jrr Timer");
				// if(controller.isRunNow()) {
				// controller.runTask();
				// }
				while (isRunNowInternal()) {
					waitNextRun();
					final boolean rr = isRunNowInternal();
					if (rr) {
						setLastRunTime(nextFireTime);
						controller.runTask();
					}
				}
				// start1();
				// if (changeStop) {
				// }
				// } catch (final Error e) {
				// if (Utils.isJavaVersion15OHigher) {
				// getUncaughtExceptionHandler().uncaughtException(this, e);
				// } else {
				// e.printStackTrace();
				// }
			} catch (final Throwable e) {
				log.warn(this.getName(), e);
				synchronized (lock2) {
					Timer.this.stop();
				}
			} finally {
				boolean runAgain;
				synchronized (lock2) {
					runAgain = isRunNowInternal();
					if (!runAgain) {
						waitingThreadRunning = false;
						waitingThread = null;
					}
				}
				if (runAgain) {
					run();
				} else {
					controller.stopNotifier();
				}
				// no waitingThread=null due to another thread may be runned
			}
		}
	}

	public static void setThreadName(final Thread thread, final String name) {
		thread.setName(name);
	}

	public Date getLastRunBefore() {
		if (lastRunBefore == -1) {
			return null;
		}
		return new Date(lastRunBefore);
	}

	protected void setLastRunTime(long lastRun) {
		lastRunBefore = this.lastRun;
		this.lastRun = lastRun;
	}

}