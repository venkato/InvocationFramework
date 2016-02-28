package net.sf.jremoterun.utilities.nonjdk.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Date;

/**
 * This class can executes specified task at specified date.
 */
public class DateCountDown implements Serializable {

	private static final long serialVersionUID = -923380147344681949L;

	private static final Logger log = LogManager.getLogger();

	private Runnable listener;

	private Date startDate;

	private transient boolean isWaitDate = false;

	// remove final after clean up
	private volatile transient Thread waitThread;

	public DateCountDown(final Runnable listener, final Date startDate) {
		this.listener = listener;
		this.startDate = startDate;
		if (isWaitDate) {
			return;
		}
		// TODO add DateTimer thread name
		waitThread = new Thread() {

			@Override
			public void run() {
				try {
					isWaitDate = true;
					waitDate(startDate);
					isWaitDate = false;
					listener.run();
				} catch (final InterruptedException e) {
				} finally {
					isWaitDate = false;
				}
			}
		};
		Timer.setThreadName(waitThread, "Date countdown");
		waitThread.start();
	}

	public boolean isWaitDate() {
		return isWaitDate;
	}

	public synchronized void stopWaitDate() {
		if (isWaitDate) {
			waitThread.interrupt();
			// waitThread.join();
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @deprecated Better is create new DateTimer
	 */
	@Deprecated
	public void setStartDate(final Date date) {
		this.startDate = date;
	}

	/**
	 * @return date of executing this method
	 */
	public static Date waitDate(final long date) throws MyInterruptedException1 {
		final long currcentTime = System.currentTimeMillis();
		final long waitTime = date - currcentTime;
		if (waitTime > 0) {
			try {
				Thread.sleep(waitTime);
			} catch (final InterruptedException e) {
				throw new MyInterruptedException1(e);
			}
			return new Date(date);
		}
		return new Date(currcentTime);
	}

	public static Date waitDate(final Date date) throws InterruptedException {
		return waitDate(date.getTime());
	}

	public Runnable getListener() {
		return listener;
	}

	public void setListener(final Runnable listener) {
		this.listener = listener;
	}
}
