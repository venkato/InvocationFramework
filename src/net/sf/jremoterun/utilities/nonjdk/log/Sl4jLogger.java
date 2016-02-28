package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

public class Sl4jLogger {
	private static final Logger log = LogManager.getLogger();

	public static void setSl4jLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Log4jLoggerFactory log4jLoggerLogFactory = new Log4jLoggerFactory();
		StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
		org.slf4j.Logger instance = log4jLoggerLogFactory.getLogger("test");
		JrrClassUtils.setFieldValue(singleton, "loggerFactory", log4jLoggerLogFactory);
	}

}
