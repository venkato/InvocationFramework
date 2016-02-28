package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClasspathTester
import org.apache.commons.io.output.TeeOutputStream

import java.util.logging.Logger

@CompileStatic
class GeneralUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<String> defaultEnv = createDefaultEnv2()

    static Thread startLogTimer() {
        startLogTimer(3600_000)
    }

    static Thread startLogTimer(long interval) {
        Runnable r = {
            while (true) {
                Thread.sleep(interval)
                println "Log time : ${new Date().format("yyyy-MM-dd HH:mm:ss")}"
            }

        }
        Thread thread = new Thread(r, "Log time")
        thread.daemon = true
        thread.start()
        return thread
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

    static Process runNativeProcess2(String cmd, File runDir) {
        Process process = cmd.execute(defaultEnv, runDir);
        process.consumeProcessOutput(System.out, System.err);
        return process;
    }

    static Process runNativeProcessWithTimeout(String cmd, File runDir, long timeoutInSec, Runnable doIfTimeout) {
        Process process = cmd.execute(defaultEnv, runDir);
        LastByteArrayOutputStream outLast = new LastByteArrayOutputStream()
        LastByteArrayOutputStream errLast = new LastByteArrayOutputStream()
        OutputStream outTee = new TeeOutputStream(outLast, System.out)
        OutputStream errTee = new TeeOutputStream(errLast, System.err)
        process.consumeProcessOutput(outTee, errTee)
        int exitCode = -1;
        Runnable r = {
            exitCode = process.waitFor()
        }
        Thread thread = new Thread(r)
        thread.start()
        thread.join(timeoutInSec * 1000)
        if (thread.isAlive()) {
            log.info("Process still alive : ${cmd}")
            doIfTimeout.run();
        } else {
            if (exitCode != 0) {
                String errOut = errLast.toString()
                String outOut = outLast.toString()
                throw new BadExitCodeException("Bad exit ${exitCode} : ${errOut},\n${outOut}")
            }
        }
        return process
    }

    static Process runNativeProcess(String cmd) {
        return runNativeProcess(cmd, null, true);
    }

    static void waitFinish(Process process, OutputStream outStream, OutputStream errStream, boolean exceptionOnError) {
        LastByteArrayOutputStream outLast = new LastByteArrayOutputStream()
        LastByteArrayOutputStream errLast = new LastByteArrayOutputStream()
        OutputStream outTee = outStream == null ? outLast : new TeeOutputStream(outLast, outStream)
        OutputStream errTee = errStream == null ? errLast : new TeeOutputStream(errLast, errStream)
        Thread threadOut = process.consumeProcessOutputStream(outTee)
        Thread threadErr = process.consumeProcessErrorStream(errTee)
        int exitCode = process.waitFor()
        log.fine "exit code : ${exitCode}"
        threadOut.join()
        threadErr.join()
        if (exitCode != 0) {
            String errOut = errLast.toString()
            String outOut = outLast.toString()
            if (exceptionOnError) {
                throw new BadExitCodeException("Bad exit ${exitCode} : ${errOut},\n${outOut}")
            }else{
                log.info "bad exit code ${exitCode} : ${errOut},\n${outOut}"
            }
        }
    }

    static Process runNativeProcess(String cmd, File runDir, boolean exceptionOnError) {
        if (runDir != null) {
            assert runDir.exists()
        }
        Process process = cmd.execute(defaultEnv, runDir);
        waitFinish(process, System.out, System.err, exceptionOnError)
        return process;
    }


    public static void checkDiskFreeSpace(File file, long minFreeSpaceInMb) throws IOException {
        long freeSpace = file.getFreeSpace() / 1_000_000 as long;
        if (freeSpace < minFreeSpaceInMb) {
            throw new IOException("low free space " + freeSpace + " mb in " + file.getAbsolutePath());
        }
    }



}
