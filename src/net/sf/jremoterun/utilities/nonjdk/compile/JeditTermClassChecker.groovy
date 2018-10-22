package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JeditTermClassChecker implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {

    }
}
