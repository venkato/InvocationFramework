package net.sf.jremoterun.utilities.nonjdk.timer;

public interface TooShortPeriodListener {

	/**
	 * * If timerStyle == TimerStyle next task will not be executed until this
	 * method is finished.
	 */
	public void tooShortPeriod(long executionTimeOfLastTask);

}
