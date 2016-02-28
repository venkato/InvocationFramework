package net.sf.jremoterun.utilities.nonjdk.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public abstract class TimerController {

	private static final Logger log = LogManager.getLogger();

	protected abstract Date isRunNow();

	protected abstract Date getNextFireDate();

	protected abstract void runTask();

	/**
	 * Notify when timer turn off. This methods should not do big work.
	 */
	protected void stopNotifier() {
		String s = getClass().getName() + " " + new Date();
		if (this instanceof net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod) {
			final net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod new_name = (TimerPeriod) this;
			s += "\n" + new_name.getTask();
		}
		// log.info(s);
	}
}
