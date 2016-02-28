package net.sf.jremoterun.utilities.nonjdk.timer;

import net.sf.jremoterun.JrrUtils;

import javax.management.ObjectName;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public interface CronTimerMBean {

	ObjectName objectName = JrrUtils.createObjectName("jrrutilities:type=timer");

	void start();

	void stop();

	void setCronExpressions(String text) throws ParseException, IOException;

	String getCronExpressions();

	boolean runTaskNowIfNotSleep();

	Date getLastRun();

	boolean isTimerRunning();

	Date getNextFireDate();
}
