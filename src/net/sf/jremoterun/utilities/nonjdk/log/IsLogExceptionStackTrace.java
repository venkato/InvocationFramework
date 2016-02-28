package net.sf.jremoterun.utilities.nonjdk.log;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

@CompileStatic
public class IsLogExceptionStackTrace {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean isLogStackTrace(Object logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable exception){
        return true;
    }

}
