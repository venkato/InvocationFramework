package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class FoundManyException extends Exception{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    FoundManyException(String var1) {
        super(var1)
    }

    FoundManyException(String var1, Throwable var2) {
        super(var1, var2)
    }
}
