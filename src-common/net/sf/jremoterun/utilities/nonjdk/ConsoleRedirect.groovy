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


    static FileOutputStream2 setOutputWithRotationAndFormatter(File outFile, int maxDepth) {
        FileOutputStream2 outputStream2 = setOutputWithRotation(outFile, maxDepth)
        JdkLogFormatter.setLogFormatter()
        return outputStream2
    }

    static FileOutputStream2 setOutputWithRotation(File outFile, int maxDepth) {
        SetConsoleOut2.setConsoleOutIfNotInited()
        FileRotate.rotateFile(outFile, maxDepth)
        FileOutputStream2 outputStream2 = setOutputToConsoleAndFile(outFile)
        setOutputForConsleHandler(SetConsoleOut2.proxyOut)
        return outputStream2
    }

    static void setOutputForConsleHandler(OutputStream outputStream) {
        ConsoleHandler handler = JdkLogFormatter.findConsoleHandler()
        synchronized (handler) {
            JrrClassUtils.setFieldValue(handler, "writer", null)
            JrrClassUtils.invokeJavaMethod(handler, "setOutputStream", outputStream)
        }
    }

    static FileOutputStream2 setOutputToConsoleAndFile(File out) {
        return setOutputToConsoleAndFile3(out)
    }

    static FileOutputStream2 setOutputToConsoleAndFile3(File out) {
        if (outputFile != null) {
            log.warn("Output redirection was set before in ", creationCallStack)
            throw new IllegalStateException("Output redirection was set before : ${outputFile}")
        }
        return setOutputToConsoleAndFileImpl(out)
    }

    static FileOutputStream2 setOutputToConsoleAndFileImpl(File out) {
        JrrUtilities3.checkFileExist(out.parentFile)
        FileOutputStream2 out2 = new FileOutputStream2(out, false);
        RedirectOutStream.addOutStream out2
        outputFile = out
        creationCallStack = new Exception("Creation call stack")
        return out2;
    }

}
