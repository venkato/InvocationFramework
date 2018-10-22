package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class NetDebugEnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

// https://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/ReadDebug.html
    static void enableDebug() {
        System.setProperty('javax.net.debug', 'ssl:handshake:verbose:keymanager:trustmanager')
        System.setProperty('javax.net.debug', 'all')
    }


}
