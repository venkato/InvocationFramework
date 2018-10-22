package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import java.util.logging.Logger;

public class Log4jMigrateUtils {
	private static final Logger log = Logger.getLogger(Log4jMigrateUtils.class.getName());
	// private static final Logger log = logger;


	public static Level logRootLevel = Level.INFO;

	public static void setLog4jAppender() throws Exception {
		InitLogTracker.defaultTracker.addLog("resetting log4j1 configuration");
		LogManager.resetConfiguration();
		InitLogTracker.defaultTracker.addLog("log4j1 setting appender");
		Log4jIntoLog4j2Converter log4j2Converter = new Log4jIntoLog4j2Converter();
		org.apache.log4j.Logger.getRootLogger().addAppender(log4j2Converter);
		org.apache.log4j.Logger.getRootLogger().setLevel(logRootLevel);
		try {
			Log4jConfigurator.registerLog4jLoggersMBeansUsingCreateMethods();
		} catch (Exception e) {
			InitLogTracker.defaultTracker.addException("failed set logj41 to log4j2 converter",e);
			Throwable rootException = JrrUtils.getRootException(e);
			if (rootException instanceof ClassNotFoundException) {
				ClassNotFoundException cnfe = (ClassNotFoundException) rootException;
				log.info(cnfe + "");
			}

		}
	}

}
