package net.sf.jremoterun.utilities.nonjdk.timer;

import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * This class can be used to schedule task in specific period.
 * <p>
 */
public class TimerPeriod extends TimerController {

	private transient static final Logger log = LogManager.getLogger();

	private TimerStyle timerStyle;

	private Runnable task;

	protected Timer timer = new Timer(this);

	/**
	 * period in milli seconds
	 */
	private long period;

	private boolean runNow = true;

	public TimerPeriod() {

	}

	/**
	 * ignore, if already running
	 */
	public void start() {
		if (timer.isTimerRunning()) {
			log.warn("timer is running");
		} else {
			timer.start();
		}
	}

	/**
	 * Throw exception if already started
	 */
	public void start2() {
		if (timer.isTimerRunning()) {
			// log.warn("timer is running");
			throw new TimerException("already launched");
		} else {
			timer.start();
		}
	}

	/**
	 * @param period
	 *            in ms
	 * @param task
	 */
	public TimerPeriod(final long period, final Runnable task) {
		this(TimerStyle.Consecutive, task, period);
	}

	/**
	 * @param timerStyle
	 * @param task
	 * @param period
	 *            in ms
	 */
	public TimerPeriod(final TimerStyle timerStyle, final Runnable task, final long period) {
		this.timerStyle = timerStyle;
		this.task = task;
		this.period = period;
	}

	@Override
	protected Date isRunNow() {
		if (timer.getLastRun() == null || runNow) {
			return new Date();
		}
		return null;
	}

	@Override
	protected Date getNextFireDate() {
		return new Date(timer.getLastRun().getTime() + period);
	}

	protected void preRunTask() {
		runNow = false;
	}

	@Override
	protected void runTask() {
		preRunTask();
		if (task == null) {
			throw new Error("task is null");
		}
		switch (timerStyle) {
		case Consecutive:
			task.run();
			break;
		case NoWait:
			final Thread thread = new Thread(task);
			Timer.setThreadName(thread, "Jrr Timer Run");
			thread.start();
			break;
		default:
			throw new Error();
		}

	}

	public long getPeriod() {
		return period;
	}

	/**
	 * Period in ms
	 */
	public synchronized void setPeriod(final long period) {
		if (period <= 0) {
			throw new TimerException("period <= 0");
		}
		this.period = period;
		timer.reEvauateNextRun();
	}

	public boolean runTaskNowIfNotSleep() {
		runNow = true;
		if (timer.reEvauateNextRun()) {
			return true;
		}
		if (runNow == false) {
			return true;
		}
		runNow = false;
		return false;

	}

	public TimerStyle getTimerStyle() {
		return timerStyle;
	}

	public void setTimerStyle(final TimerStyle timerStyle) {
		this.timerStyle = timerStyle;
	}

	public Runnable getTask() {
		return task;
	}

	public void setTask(final Runnable task) {
		Assert.assertNotNull(task);
		this.task = task;
	}

	public void stop() {
		timer.stop();

	}

	public boolean isTimerRunning() {
		return timer.isTimerRunning();
	}

	public void taskWasRunNow() {
		timer.taskWasRunNow();
	}

	public Date getLastRun() {
		return timer.getLastRun();
	}

	@Override
	protected void stopNotifier() {

	}

	public Timer getTimerImpl() {
		return timer;
	}
}