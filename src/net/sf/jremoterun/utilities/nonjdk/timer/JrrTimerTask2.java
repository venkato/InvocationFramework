package net.sf.jremoterun.utilities.nonjdk.timer;

import net.sf.jremoterun.JrrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.MBeanServer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class JrrTimerTask2 {

	private static final Logger log = LogManager.getLogger();

	public Object lock = new Object();

	public List<net.sf.jremoterun.utilities.nonjdk.timer.Timer> timers = new Vector();

	public static MBeanServer beanServer = JrrUtils.findLocalMBeanServer();

	// public String id;

	static SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");

	public JrrTimerTask2() {
		// this.id = clazz.getSimpleName() + ",localtype=" + id;
	}

	public void addTimer(final CronTimer timer) {
		addTimer(timer.getTimer());
	}

	public void addTimer(net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod timer) {
		addTimer(timer.getTimerImpl());
	}

	public void addTimer(net.sf.jremoterun.utilities.nonjdk.timer.Timer timer) {
		synchronized (lock) {
			timers.add(timer);
		}
		// String on = JrrBeanMaker.jrrMBeansPrefix + id + ",timer=timer,date="
		// + df.format(new Date());
		// log.info(on);
		// ObjectName objectName = new ObjectName(on);
		// beanServer.registerMBean(new MBeanFromJavaBean(taskExecutor),
		// objectName);
	}

	public void stopAllTimers() {
		synchronized (lock) {
			for (net.sf.jremoterun.utilities.nonjdk.timer.Timer timer : timers) {
				timer.stop();
			}
		}
	}

	public void addTimerPeriodAndStart(TimerPeriod timerPeriod) {
		timerPeriod.getTimerImpl().setLastRun(getLastRun());
		timerPeriod.start2();
	}

	public Date getLastRun() {
		ArrayList<net.sf.jremoterun.utilities.nonjdk.timer.Timer> arrayList;
		synchronized (lock) {
			arrayList = new ArrayList(timers);
		}
		Date lastDate = null;
		for (net.sf.jremoterun.utilities.nonjdk.timer.Timer timer : arrayList) {
			Date lastRun2;
			if (timer.isTimerRunning()) {
				if (timer.isInSleep()) {
					lastRun2 = timer.getLastRun();
				} else {
					lastRun2 = timer.getLastRunBefore();

				}
			} else {
				lastRun2 = timer.getLastRun();
			}
			if (lastRun2 != null) {
				if (lastDate == null) {
					lastDate = lastRun2;
				} else {
					lastDate = lastRun2.after(lastDate) ? lastRun2 : lastDate;
				}
			}
		}
		return lastDate;
	}

	public net.sf.jremoterun.utilities.nonjdk.timer.Timer getRunningTimer() {
		synchronized (lock) {
			for (net.sf.jremoterun.utilities.nonjdk.timer.Timer timer : timers) {
				if (timer.isTimerRunning()) {
					return timer;
				}
			}
		}
		return null;
	}

	public List<Timer> getTimers() {
		synchronized (lock) {
			return new ArrayList(timers);
		}
	}

	// public String getId() {
	// return id;
	// }
}
