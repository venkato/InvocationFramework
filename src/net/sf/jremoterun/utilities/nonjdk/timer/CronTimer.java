package net.sf.jremoterun.utilities.nonjdk.timer;

import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class can be used to schedule task in specific period.
 * <p>
 */
public class CronTimer extends TimerController implements CronTimerMBean {

	private transient final static Logger log = LogManager.getLogger();

	private TimerStyle timerStyle = TimerStyle.Consecutive;

	private Runnable task;

	protected net.sf.jremoterun.utilities.nonjdk.timer.Timer timer = new net.sf.jremoterun.utilities.nonjdk.timer.Timer(this);

	private boolean runNow = false;

	private List<CronExpression> cronExpressions;

	public void start() {
		if (timer.isTimerRunning()) {
			// log.warn("timer is running");
			throw new TimerException("already launched");
		}
		if (cronExpressions.size() == 0) {
			throw new TimerException("cronExpressions size is null");
		}
		timer.start();
	}

	public net.sf.jremoterun.utilities.nonjdk.timer.Timer getTimer() {
		return timer;
	}

	public void setCronExpressions(final String text) throws ParseException, IOException {
		final BufferedReader reader = new BufferedReader(new StringReader(text.trim()));
		String line = reader.readLine();
		final List<CronExpression> cronExpressions = new ArrayList();
		while (line != null) {
			if (line.length() != 0) {
				final CronExpression cronExpression = new CronExpression(line);
				cronExpressions.add(cronExpression);
			}
			line = reader.readLine();
		}
		setCronExpressions(cronExpressions);
	}

	public List<CronExpression> getCronExpressionsNative() {
		return cronExpressions;
	}

	public String getCronExpressions() {
		final StringBuffer result = new StringBuffer();
		boolean first = true;
		for (final CronExpression cronExpression : cronExpressions) {
			if (first) {
				first = false;
			} else {
				result.append("\r\n");
			}
			result.append(cronExpression);
		}
		return result.toString();
	}

	public void setCronExpressions(final List<CronExpression> cronExpressions) {
		this.cronExpressions = cronExpressions;
		timer.reEvauateNextRun();
	}

	public CronTimer(final Runnable task) {
		this.task = task;
		// this.cronExpressions = cronExpression;
	}

	@Override
	protected Date isRunNow() {
		if (runNow) {
			return new Date();
		}
		return null;
	}

	@Override
	public Date getNextFireDate() {
		final Date now = new Date();
		Date firstDate = null;
		for (final CronExpression cronExpression : cronExpressions) {
			final Date firstDate2 = cronExpression.getNextValidTimeAfter(now);
			if (firstDate == null || firstDate.after(firstDate2)) {
				firstDate = firstDate2;
			}
		}
		return firstDate;
	}

	protected void preRunTask() {
		runNow = false;
	}

	@Override
	protected void runTask() {
		preRunTask();
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
		// TODO Auto-generated method stub

	}
}