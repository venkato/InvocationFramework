package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.Thread.UncaughtExceptionHandler;

@CompileStatic
public class SimpleUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger log = LogManager.getLogger();

    public static volatile boolean inited = false;

    public void uncaughtException(final Thread t, final Throwable e) {
        log.info(t, e);
        JrrUtilities.showException("thread " + t.getId() + " " + t.getName(), e);
    }

    public static void setDefaultUncaughtExceptionHandler() {
        if (!inited) {
            inited = true;
            UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (defaultUncaughtExceptionHandler == null) {
                Thread.setDefaultUncaughtExceptionHandler(new SimpleUncaughtExceptionHandler());
            } else {
                log.info("found not null defaultUncaughtExceptionHandler " + defaultUncaughtExceptionHandler);
            }
        }
    }
}