package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;

import java.lang.Thread.UncaughtExceptionHandler
import java.util.logging.Logger;

@CompileStatic
public class SimpleUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static volatile boolean inited = false;

    public void uncaughtException(final Thread t, final Throwable e) {
        log.severe(t, e);
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