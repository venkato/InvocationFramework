package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.log.IsLogExceptionStackTrace

import java.util.logging.Logger

@CompileStatic
class IsLogExceptionStackTraceIdea extends IsLogExceptionStackTrace {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean isLogStackTrace(Object logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable exception) {
        if ("com.intellij.openapi.wm.impl.ToolWindowImpl".equals(location.className) &&
                "setIcon".equals(location.methodName)) {
            return false;
        }
        return super.isLogStackTrace(logRecord, sb, stackTraces, location, exception)
    }
}
