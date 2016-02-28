package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook

import java.util.logging.Logger

@CompileStatic
class Redirector implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    @Override
    void run() {
        f1()
    }

    void f1() {
//        File f = "c:\\1\\3\\idea_b_logs\\a.log" as File
//        ConsoleRedirect.setOutputWithRotationAndFormatter(f, 95)
        LogExitTimeHook.addShutDownHook()
    }
}
