package net.sf.jremoterun.utilities.nonjdk.timer;

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

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static void print(String msgs) {
        System.out.println(msgs);
    }

}
