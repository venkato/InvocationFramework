package net.sf.jremoterun.utilities.nonjdk.timer;

import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * This class can be used to schedule task in specific period and adds some
 * features for {@link Timer} class. <br>
 * If execution time of task would be more than period, then Timer will set
 * period to execution time of the task. This strategy can be changed if replace
 * TooShortPeriodListener.
 */
public class AdjustPeriodTimer extends TimerPeriod {

	private static final Logger log = LogManager.getLogger();

	private long executionTimeOfLastTask = -1;

	private Runnable task2;

	private long minExecTime = -1;

	private long maxExecTime = -1;

	private long executesCount;

	private TooShortPeriodListener tooShortPeriodListener = new TooShortPeriodListener() {

		public void tooShortPeriod(final long executionTimeOfLastTask) {
			setPeriod(executionTimeOfLastTask);
			log.info("period was increased to " + executionTimeOfLastTask);

		}
	};

	public AdjustPeriodTimer() {
		super.setTask(new TimerTask());
	}

	/**
	 *
	 * @param updateTime in ms
	 * @param task
	 */
	public AdjustPeriodTimer(final long updateTime, final Runnable task) {
		this(updateTime, TimerStyle.Consecutive, task);
	}

	/**
	 * @param updateTime milli seconds
	 * @param timerStyle
	 * @param task
	 */
	public AdjustPeriodTimer(final long updateTime, final TimerStyle timerStyle, final Runnable task) {
		// super(timerStyle, new TimerTask(), updateTime);
		setPeriod(updateTime);
		setTask(task);
		setTimerStyle(timerStyle);
		super.setTask(new TimerTask());
	}

	public long getExecutionTimeOfLastTask() {
		return executionTimeOfLastTask;
	}

	public TooShortPeriodListener getTooShortPeriodListener() {
		return tooShortPeriodListener;
	}

	@Override
	public void setTask(final Runnable task) {
		Assert.assertNotNull(task);
		this.task2 = task;
	}

	/**
	 * If executionTimeTooMachListener is not default then period will not be
	 * changed by Timer.
	 */
	public void setTooShortPeriodListener(final TooShortPeriodListener executionTimeTooMachListener) {
		this.tooShortPeriodListener = executionTimeTooMachListener;
	}

	@Override
	public Runnable getTask() {
		return task2;
	}

	private class TimerTask implements Runnable {

		public void run() {
			Date localLastRun = timer.getLastRun();
			// log.info(localLastRun);
			task2.run();
			executesCount++;
			final long currentTime = System.currentTimeMillis();
			if (getTimerStyle() == net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle.Consecutive) {
				localLastRun = timer.getLastRun();
			}
			long executionTimeOfLastTask2 = currentTime - localLastRun.getTime();
			executionTimeOfLastTask = executionTimeOfLastTask2;
			if (minExecTime == -1) {
				minExecTime = executionTimeOfLastTask2;
			} else {
				minExecTime = Math.min(executionTimeOfLastTask2, minExecTime);
			}
			if (maxExecTime == -1) {
				maxExecTime = executionTimeOfLastTask2;
			} else {
				maxExecTime = Math.min(executionTimeOfLastTask2, maxExecTime);
			}
			final long period = getPeriod();
			if ((executionTimeOfLastTask2 > period) && (tooShortPeriodListener != null)) {
				tooShortPeriodListener.tooShortPeriod(executionTimeOfLastTask2);
			}
		}
	}

	public long getMinExecTime() {
		return minExecTime;
	}

	public long getMaxExecTime() {
		return maxExecTime;
	}

	public long getExecutesCount() {
		return executesCount;
	}
}
