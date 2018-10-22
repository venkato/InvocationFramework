package net.sf.jremoterun.utilities.nonjdk.nativeprocess

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.BadExitCodeException
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.LastByteArrayOutputStream

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class NativeProcessResult {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public final Process process;
    public final LastByteArrayOutputStream outLast = new LastByteArrayOutputStream();
    public final LastByteArrayOutputStream errLast = new LastByteArrayOutputStream();
    public final FilterOutputStreamJrr out2 = new FilterOutputStreamJrr(outLast);
    public final FilterOutputStreamJrr err2 = new FilterOutputStreamJrr(errLast);
    public Thread threadOut;
    public Thread threadErr;
    public volatile Integer exitCode;
    public final Date startDate = new Date();
    public boolean exceptionOnError = true;
    private volatile boolean waitAsync = false;
    public volatile boolean continueRunning = true;
    public volatile long timeoutInSec = 10

    static List<String> defaultEnv = createDefaultEnv2()


    public Thread thread = new Thread() {
        @Override
        void run() {
            doWaitImpl()
        }
    };


// simple runner
    static NativeProcessResult runNativeProcessAndWait(String cmd) {
        runNativeProcessAndWait(cmd,null)
    }
    static NativeProcessResult runNativeProcessAndWait(String cmd, File runDir) {
        if (runDir != null) {
            assert runDir.exists()
        }
        Process process1 = cmd.execute(defaultEnv, runDir);
        NativeProcessResult processResult = new NativeProcessResult(process1) {
            @Override
            void onTimeout() {
                log.info "process running too long : ${cmd}"
            }
        };
        processResult.consumeOutStreamSysout()
        processResult.waitWithPeriodicCheck()
        return processResult
    }


    static List<String> createEnvFromMap(Map<String, String> getenv) {
        return getenv.entrySet().collect { "${it.key}=${it.value}".toString() };
    }

    static List<String> createDefaultEnv2() {
        return createEnvFromMap(createDefaultEnv())
    }

    static Map<String, String> createDefaultEnv() {
        Map<String, String> getenv = new Hashtable<>(System.getenv());
        getenv.remove('GROOVY_OPTS')
        getenv.remove('groovypath')
        getenv = getenv.findAll { it.key != null && it.key.length() > 0 && it.value != null && it.value.length() > 0 }
        return getenv;
    }


    NativeProcessResult(Process process) {
        this.process = process
    }


    void doWaitImpl() {
        try {
            exitCode = process.waitFor();
            waitIOThreadFinished()
            if (waitAsync) {
                onFinished();
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "failed wait", e)
        }
    }

    void consumeOutStreamSysout() {
        out2.addStream(new NonClosableStream(System.out))
        err2.addStream(new NonClosableStream(System.err))
    }


    void addWriteOutToFile(File outputFile, int rotationDepth) {
        FileRotate.rotateFile(outputFile, rotationDepth)
        BufferedOutputStream outputStream2 = outputFile.newOutputStream()
        out2.addStream(outputStream2)
        err2.addStream(outputStream2)
    }

    void consumeOutStream() {
        if (threadOut != null) {
            throw new Exception("Output already set")
        }
        threadOut = process.consumeProcessOutputStream(out2);
        threadErr = process.consumeProcessErrorStream(err2);
    }

    void onTimeout() {}

    void onFinishedFine() {}

    void closeOutStreams() {
        try {
            out2.flush()
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed flush out stream", e)
        }
        try {
            out2.flush()
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed flush err stream", e)
        }
        try {
            out2.close()
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed close out stream", e)
        }
        try {
            err2.close()
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed close err stream", e)
        }
    }

    void onFinished() {
        closeOutStreams()
        if (exitCode == 0) {
            onFinishedFine()
        } else {
            onBadExitCode()
        }
    }

    void onBadExitCode() {
        if (exceptionOnError) {
            String errOut = errLast.toString();
            String outOut = outLast.toString();
            throw new BadExitCodeException("Bad exit code = ${exitCode} : ${errOut},\n${outOut}");
        } else {
            String errOut = errLast.toString()
            String outOut = outLast.toString()
            log.info "bad exit code ${exitCode} : ${errOut},\n${outOut}"
        }
    }


    void waitAsyncM() {
        waitAsync = true
        if (threadOut == null) {
            consumeOutStream();
        }
        thread.start();
    }


    void waitWithPeriodicCheck() {
        if (threadOut == null) {
            consumeOutStream();
        }

        thread.start();
        while (continueRunning) {
            thread.join(timeoutInSec * 1000);
            if (thread.isAlive()) {
                onTimeout()
            } else {
                break
            }
        }

        assert !thread.isAlive()
        onFinished()
    }

    void waitIOThreadFinished() {
        threadOut.join()
        threadErr.join()
    }

    @Override
    String toString() {
        return "alive=${thread.isAlive()} exitCode=${exitCode}, errStream=${errLast}, outStream=${outLast}";
    }

}
