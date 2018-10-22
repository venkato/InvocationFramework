package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ProblemInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String msg

    Throwable stackTrace;

    @Override
    String toString() {
        if (stackTrace == null || stackTrace instanceof JustStackTrace) {
            return msg
        }
        return "${msg} ${stackTrace}"
    }
}
