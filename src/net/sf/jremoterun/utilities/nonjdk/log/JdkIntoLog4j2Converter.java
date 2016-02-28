package net.sf.jremoterun.utilities.nonjdk.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.jul.DefaultLevelConverter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JdkIntoLog4j2Converter extends Handler {

	public static DefaultLevelConverter defaultLevelConverter = new DefaultLevelConverter();

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {

	}

	@Override
	public void publish(final LogRecord logRecord) {
		Logger logger = LogManager.getLogger(logRecord.getLoggerName());
		logger.log(defaultLevelConverter.toLevel(logRecord.getLevel()), logRecord.getMessage(), logRecord.getThrown());
	}


}
