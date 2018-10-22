package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.logging.log4j.LogManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class to simulate slow connections by slowing down the system
 */
@CompileStatic
public class SlowLinkSimulator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final int delayBytes;

    public final int delayTime;

    public int currentBytes;

    public int totalBytes;

    /**
     * construct
     *
     * @param delayBytes
     *            bytes per delay; set to 0 for no delay
     * @param delayTime
     *            delay time per delay in milliseconds
     */
    public SlowLinkSimulator(final int delayBytes, final int delayTime) {
        this.delayBytes = delayBytes;
        this.delayTime = delayTime;
    }

    /**
     * construct by copying delay bytes and time, but not current count of
     * bytes
     *
     * @param that
     *            source of data
     */
    public SlowLinkSimulator(final SlowLinkSimulator that) {
        this.delayBytes = that.delayBytes;
        this.delayTime = that.delayTime;
    }

    /**
     * how many bytes have gone past?
     *
     * @return
     */
    public int getTotalBytes() {
        return totalBytes;
    }

    /**
     * log #of bytes pumped. Will pause when necessary. This method is not
     * synchronized
     *
     * @param bytes
     */
    public void pump(final int bytes) {
        totalBytes += bytes;
        if (delayBytes == 0) {
            // when not delaying, we are just a byte counter
            return;
        }
        currentBytes += bytes;
        if (currentBytes > delayBytes) {
            // we have overshot. lets find out how far
            final int delaysize = currentBytes / delayBytes;
            final long delay = delaysize * (long) delayTime;
            // move byte counter down to the remainder of bytes
            currentBytes = currentBytes % delayBytes;
            if (delay != 0) {
                // now wait
                try {
                    Thread.sleep(delay);
                } catch (final InterruptedException e) {
                    log.log(Level.INFO,"",e);
                }
            }
        }
    }

    /**
     * get the current byte count
     *
     * @return
     */
    public int getCurrentBytes() {
        return currentBytes;
    }

    /**
     * set the current byte count
     *
     * @param currentBytes
     */
    public void setCurrentBytes(final int currentBytes) {
        this.currentBytes = currentBytes;
    }

}
