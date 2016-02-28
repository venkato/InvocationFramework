package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.SwingUtilities
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@CompileStatic
class Scheduler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10)

    static void scheduleOnceInSwingThread(long delay, TimeUnit timeUnit,Runnable r ) {
        Runnable r2 = {
            SwingUtilities.invokeLater(r)
        }
        scheduledThreadPoolExecutor.schedule(r2, delay, timeUnit)
    }

    static void scheduleOnceInAnyThread(long delay, TimeUnit timeUnit,Runnable r) {
        scheduledThreadPoolExecutor.schedule(r, delay, timeUnit)
    }

}
