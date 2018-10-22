package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.channels.JrrChannelExec
import org.apache.commons.io.output.TeeOutputStream
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

@CompileStatic
class SshCommandExec {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int maxDataLength = 1024;

    public JrrChannelExec execChannel;
    public String cmd;
    public ByteArrayOutputStream err2 = new ByteArrayOutputStream()
    public ByteArrayOutputStream out1 = new ByteArrayOutputStream()
    public ByteArrayOutputStream err1 = new ByteArrayOutputStream()
    public final Object lock = new Object();

    public String err2S
    public String outS
    public String err1S;

    SshCommandExec(JrrChannelExec channelExec, String cmd) {
        this.execChannel = channelExec
        this.cmd = cmd
    }

    void onFinish() {

        synchronized (lock) {
            lock.notifyAll()
        }
    }


    void printExitData() {

        if (true) {
            outS = out1.toString().trim()
            if (StringUtils.isEmpty(outS)) {
                outS = null
            } else {
                if (outS.length() > maxDataLength) {
                    outS = outS.substring(outS.length() - maxDataLength);
                }
                log.info "out = ${outS}"
            }
        }

        if (true) {
            err1S = err1.toString().trim()
            if (StringUtils.isEmpty(err1S)) {
                err1S = null
            } else {
                if (err1S.length() > maxDataLength) {
                    err1S = err1S.substring(err1S.length() - maxDataLength);
                }
                log.info "err1 = ${err1S}"
            }
        }

        err2S = err2.toString().trim()
        if (StringUtils.isEmpty(err2S)) {
            err2S = null
        } else {
            if (err2S.length() > maxDataLength) {
                err2S = err1S.substring(err2S.length() - maxDataLength);
            }
            log.info "err2 = ${err2S}"
        }


        log.info "exit code : ${execChannel.getExitStatus()}"
    }


    void setStreamsWithParallelLogToSysout() {
        TeeOutputStream err5 = new TeeOutputStream(System.out, err2);
        TeeOutputStream out5 = new TeeOutputStream(System.out, out1);
        TeeOutputStream extOut5 = new TeeOutputStream(System.out, err1);


        execChannel.setErrStream(err5, true)
        execChannel.setOutputStream(out5, true)
        execChannel.setExtOutputStream(extOut5, true)
    }

    void startAndWait() {
        startAndWait(0)
    }

    void startAndWait(long waitTimeOut) {
        synchronized (lock) {
            start();
            lock.wait(waitTimeOut)
        }
        printExitData();
        checkExitCodeAndThrowExc()
        log.info "finished2"
    }

    void checkExitCodeAndThrowExc() {
        int status = execChannel.getExitStatus()
        if (status != 0) {
            throw new SshBadExitCodeException("status = ${status}, details : ${err1S}")
        }
    }

    void start() {
        setStreamsWithParallelLogToSysout()
        Runnable r = { onFinish() }
        execChannel.setExitListener(r)

        //InputStream inputStream = execChannel.getInputStream()

        execChannel.setCommand(cmd);
        execChannel.connect()
        log.info "cmd started : ${cmd}"
    }
}
