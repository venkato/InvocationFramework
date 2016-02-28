package net.sf.jremoterun.utilities.nonjdk.timer;

/**
 * Set <code>Consecutive</code> style if you want that you applications will be
 * more reliable, If previous task is not finished next task not started.
 */
public enum TimerStyle {
	/**
	 * Execute tasks in one thread i.e. when previous task has finished. Timer
	 * will run tasks in period if execution of task <= period.
	 */
	Consecutive,
	/**
	 * In this case Timer will start new thread, which starts each task in
	 * separate thread.
	 */
	NoWait
}
