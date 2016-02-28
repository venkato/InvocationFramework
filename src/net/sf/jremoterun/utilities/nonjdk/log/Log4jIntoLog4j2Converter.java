package net.sf.jremoterun.utilities.nonjdk.log;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Log4jIntoLog4j2Converter extends AppenderSkeleton {


    public static Map<org.apache.log4j.Level, Level> log4j1ToLog4j2Map = new HashMap<org.apache.log4j.Level, Level>() {
        {
            put(org.apache.log4j.Level.ALL, Level.ALL);
            put(org.apache.log4j.Level.DEBUG, Level.DEBUG);
            put(org.apache.log4j.Level.ERROR, Level.ERROR);
            put(org.apache.log4j.Level.FATAL, Level.FATAL);
            put(org.apache.log4j.Level.INFO, Level.INFO);
            put(org.apache.log4j.Level.OFF, Level.OFF);
            put(org.apache.log4j.Level.TRACE, Level.TRACE);
            put(org.apache.log4j.Level.WARN, Level.WARN);
        }
    };


    public static Map<Level, org.apache.log4j.Level> log4j2ToLog4j1Map = MapUtils.invertMap(log4j1ToLog4j2Map);

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(LoggingEvent logRecord) {
        Logger logger = LogManager.getLogger(logRecord.getLoggerName());
        Level level = log4j1ToLog4j2Map.get(logRecord.getLevel());
        if (logger.isEnabled(level)) {
            ThrowableInformation throwableInformation = logRecord.getThrowableInformation();
            Throwable th = throwableInformation == null ? null : throwableInformation.getThrowable();
            logger.log(log4j1ToLog4j2Map.get(logRecord.getLevel()), logRecord.getMessage(), th);
        }
        // logmana
    }

}
