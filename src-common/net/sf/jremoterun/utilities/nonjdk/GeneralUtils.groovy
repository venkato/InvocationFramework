package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class GeneralUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static long defaultLogIntervalInSeconds = 3600


    static Thread startLogTimer() {
        startLogTimer(defaultLogIntervalInSeconds * 1000)
    }

    static Thread startLogTimer(long interval) {
        Runnable r = {
            while (true) {
                Thread.sleep(interval)
                println "Log time : ${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())}"
            }

        }
        Thread thread = new Thread(r, "Log time")
        thread.daemon = true
        thread.start()
        return thread
    }

    static void checkDiskFreeSpace(File file, long minFreeSpaceInMb) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long freeSpace = file.getFreeSpace() / 1_000_000 as long;
        if (freeSpace < minFreeSpaceInMb) {
            throw new IOException("low free space " + freeSpace + " mb in " + file.getAbsolutePath());
        }
    }

    static Properties readPropsFromFile(File file) {
        assert file.exists()
        Properties props = new Properties()
        BufferedInputStream inputStream = file.newInputStream()
        try {
            props.load(inputStream)
        } finally {
            try {
                inputStream.close()
            } catch (Exception e) {
                log.log(Level.INFO, "failed close ${file}", e);
            }
        }
    }

    /*

    @Deprecated
    static List<String> defaultEnv = NativeProcessResult.createDefaultEnv2()

    @Deprecated
    static List<String> createDefaultEnv2() {
        return NativeProcessResult.createDefaultEnv2()
    }

    @Deprecated
    static Map<String, String> createDefaultEnv() {
        return NativeProcessResult.createDefaultEnv();
    }

    @Deprecated
    static Process runNativeProcess2(String cmd, File runDir) {
        Process process = cmd.execute(defaultEnv, runDir);
        process.consumeProcessOutput(System.out, System.err);
        return process;
    }

    @Deprecated
    static Process runNativeProcessWithTimeout(String cmd, File runDir, long timeoutInSec, Runnable doIfTimeout) {
        Process process = cmd.execute(defaultEnv, runDir);
        NativeProcessResult processResult = new NativeProcessResult(process) {
            @Override
            void onTimeout() {
                log.info("process running long : ${cmd}")
                doIfTimeout.run()
            }
        }
        processResult.out2.addNonClosableStream(System.out)
        processResult.err2.addNonClosableStream(System.err)
        processResult.timeoutInSec = timeoutInSec
        processResult.waitWithPeriodicCheck()
        return process
    }

    @Deprecated
    static Process runNativeProcess(String cmd) {
        return runNativeProcess(cmd, null, true);
    }

    @Deprecated
    static void waitFinish(Process process, OutputStream outStream, OutputStream errStream, boolean exceptionOnError) {
        NativeProcessResult nativeProcessResult = new NativeProcessResult(process) {
            @Override
            void onTimeout() {
                log.info "process running too long"
            }
        }
        if (outStream != null) {
            nativeProcessResult.out2.addNonClosableStream(outStream)
        }
        if (errStream != null) {
            nativeProcessResult.err2.addNonClosableStream(errStream)
        }
        nativeProcessResult.exceptionOnError = exceptionOnError
    }

    @Deprecated
    static Process runNativeProcess(String cmd, File runDir, boolean exceptionOnError) {
        if (runDir != null) {
            assert runDir.exists()
        }
        Process process = cmd.execute(defaultEnv, runDir);
        waitFinish(process, System.out, System.err, exceptionOnError)
        return process;
    }

    @Deprecated
    static Process runNativeProcessRedirectOutputToFile(String cmd, File runDir, boolean exceptionOnError, File outputFile, int rotationDepth) {
        if (runDir != null) {
            assert runDir.exists()
        }
        FileRotate.rotateFile(outputFile, rotationDepth)
        BufferedOutputStream outputStream2 = outputFile.newOutputStream()
        Process process = cmd.execute(defaultEnv, runDir);
        try {
            waitFinish(process, outputStream2, outputStream2, exceptionOnError)
        } finally {
            try {
                outputStream2.flush()
                outputStream2.close()
            } catch (Throwable e) {
                log.info("failed close file : ${outputFile}", e)
            }

        }
        return process;
    }

*/

}
