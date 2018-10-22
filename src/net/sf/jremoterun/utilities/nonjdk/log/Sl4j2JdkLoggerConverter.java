package net.sf.jremoterun.utilities.nonjdk.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sl4j2JdkLoggerConverter {
    private static final Logger log = LogManager.getLogger();

    public static void setSl4jLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Sl4jLoggerCommon.setLoggerImpl(new org.slf4j.impl.JDK14LoggerFactory());
    }

}
