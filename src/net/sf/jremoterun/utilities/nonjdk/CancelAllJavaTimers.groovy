package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class CancelAllJavaTimers implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        cancelAllJavaTimers()
    }


    static void cancelAllJavaTimers() {
        Set<Thread> threads = Thread.allStackTraces.keySet().findAll { it.class.name == 'java.util.TimerThread' }
//        threads.each {it.@daemon = true}
        threads.each { cancelTimerThread(it) }
        log.info "cancelled java timers : ${threads.size()}"
    }

    static void cancelTimerThread(Thread ts) {
        Object queue = JrrClassUtils.getFieldValue(ts, 'queue')
        synchronized (queue) {
            JrrClassUtils.setFieldValue(ts, 'newTasksMayBeScheduled', false)
            JrrClassUtils.invokeJavaMethod(queue, 'clear')
            queue.notify();
        }
    }

}
