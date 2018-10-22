package net.sf.jremoterun.utilities.nonjdk.log;

import groovy.transform.CompileStatic;

import java.util.logging.Level;
import java.util.logging.Logger;

// !! must be java file to compile in idea
@CompileStatic
public class JdkLoggerExtentionClass {

    public static void info(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.INFO, msg, exception);
    }


    public static void fine(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.FINE, msg, exception);
    }


    public static void warn(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.WARNING, msg, exception);
    }


    public static void severe(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.SEVERE, msg, exception);
    }


    public static void error(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.SEVERE, msg, exception);
    }

    public static void loge(Logger logger, Level level, Object msg, Throwable exception) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertObjectToString(msg), exception);
        }
    }

    private static String convertObjectToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }


    public static void log2(Logger logger, Level level, Object msg) {
        if (logger.isLoggable(level)) {
            logger.log(level, convertObjectToString(msg));
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


    // sometimes exception can't be found, hope this would help
    public static void error3(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.SEVERE, msg, exception);
    }


    public static void warn3(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.WARNING, msg, exception);
    }

    public static void info3(Logger logger, Object msg, Throwable exception) {
        loge(logger, Level.INFO, msg, exception);
    }

    public static void debug3(Logger logger, Object msg,Throwable exception) {
        loge(logger, Level.FINE, msg, exception);
    }




}
