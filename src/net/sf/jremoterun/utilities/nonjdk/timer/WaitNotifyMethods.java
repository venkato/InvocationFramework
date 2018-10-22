package net.sf.jremoterun.utilities.nonjdk.timer;

import groovy.transform.CompileStatic;

@CompileStatic
public class WaitNotifyMethods {



    public static void wait(final Object lock) throws InterruptedException {
        lock.wait();
    }

    public static void wait(final Object lock, final long timeout) throws InterruptedException {
        lock.wait(timeout);
    }

    public static void notify(final Object lock) {
        lock.notify();
    }

    public static void notifyAll(final Object lock) {
        lock.notifyAll();
    }



}
