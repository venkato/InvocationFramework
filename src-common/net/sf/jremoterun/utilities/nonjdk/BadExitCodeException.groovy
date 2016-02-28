package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class BadExitCodeException extends Exception{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    BadExitCodeException(String var1) {
        super(var1)
    }
}
