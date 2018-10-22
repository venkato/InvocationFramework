package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.Date;
import java.util.logging.Logger;

@CompileStatic
public class LogExitTimeHook implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static volatile boolean added = false;
    public static volatile boolean systemExitCalled = false;

    public static void addShutDownHook() {
        if (added) {

        } else {
            added = true;
            Thread t = new Thread(new LogExitTimeHook(), "Vm exit logger");
            Runtime.getRuntime().addShutdownHook(t);
        }
    }

    @Override
    public void run() {
        systemExitCalled = true
        System.out.println("exit " + new Date());
        log.info(new Date().toString());
    }

}
