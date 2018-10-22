package net.sf.jremoterun.utilities.nonjdk.timer;

import groovy.transform.CompileStatic;

@CompileStatic
public class WaitNotifyMethods2 {

    public Object lock = new Object();


    public void wait2() throws InterruptedException {
        lock.wait();
    }

    public void wait2(final long timeout) throws InterruptedException {
        lock.wait(timeout);
    }

    public void notify2() {
        lock.notify();
    }

    public void notifyAll2() {
        lock.notifyAll();
    }


}
