package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.FileOutputStream2
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities3
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.nonjdk.FileRotate

import java.util.logging.ConsoleHandler
import java.util.logging.Logger

@CompileStatic
class ConsoleRedirect {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile File outputFile

    public static volatile Exception creationCallStack;


    static void setOutputWithRotationAndFormatter(File outFile, int maxDepth) {
        setOutputWithRotation(outFile, maxDepth)
        JdkLogFormatter.setLogFormatter()
    }

    static void setOutputWithRotation(File outFile, int maxDepth) {
        SetConsoleOut2.setConsoleOutIfNotInited()
        FileRotate.rotateFile(outFile, maxDepth)
        setOutputToConsoleAndFile(outFile)
        setOutputForConsleHandler(SetConsoleOut2.proxyOut)
    }

    static void setOutputForConsleHandler(OutputStream outputStream) {
        ConsoleHandler handler = JdkLogFormatter.findConsoleHandler()
        synchronized (handler) {
            JrrClassUtils.setFieldValue(handler, "writer", null)
            JrrClassUtils.invokeJavaMethod(handler, "setOutputStream", outputStream)
        }
    }

    static void setOutputToConsoleAndFile(File out) {
        setOutputToConsoleAndFile3(out)
    }

    static void setOutputToConsoleAndFile3(File out) {
        if (outputFile != null) {
            log.warn("Output redirection was set before in ", creationCallStack)
            throw new IllegalStateException("Output redirection was set before : ${outputFile}")
        }
        setOutputToConsoleAndFileImpl(out)
    }

    static void setOutputToConsoleAndFileImpl(File out) {
        JrrUtilities3.checkFileExist(out.parentFile)
        FileOutputStream2 out2 = new FileOutputStream2(out, false);
        RedirectOutStream.addOutStream out2
        outputFile = out
        creationCallStack = new Exception("Creation call stack")
    }

}
