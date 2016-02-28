package net.sf.jremoterun.utilities.nonjdk.log;

import groovy.transform.CompileStatic;

import java.util.logging.Level;
import java.util.logging.Logger;

// !! must be java file to compile in idea
@CompileStatic
public class JdkLoggerExtentionClass {

    public static void info(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.INFO, msg.toString(), exception);
    }


    public static void fine(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.FINE, msg.toString(), exception);
    }


    public static void warn(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.WARNING, msg.toString(), exception);
    }


    public static void severe(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.SEVERE, msg.toString(), exception);
    }

    public static void loge(Logger logger, Level level, Object msg, Throwable exception) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg == null ? null : msg.toString(), exception);
        }
    }


    public static void log2(Logger logger, Level level, Object msg) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg == null ? null : msg.toString());
        }
    }




    public static void info2(Logger logger, Object msg) {
        log2(logger, Level.INFO, msg);
    }


    public static void debug(Logger logger, Object msg) {
        log2(logger, Level.FINE, msg);
    }


    public static void warn(Logger logger, Object msg) {
        log2(logger, Level.WARNING, msg);
    }


    public static void error(Logger logger, Object msg) {
        log2(logger, Level.WARNING, msg);
    }


}
