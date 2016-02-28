package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.PrintExceptionListener
import org.fusesource.jansi.AnsiConsole

import java.util.logging.Logger

@CompileStatic
class SetConsoleColoring {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static volatile boolean ansibleInstalled = false

    static void setConsoleColoringWithOutRedirect(File outFile,int maxDepth) {
        SetConsoleColoring.installAnsible()
        def occured = GroovyMethodRunnerParams.instance.onExceptionOccured;
        if (occured.class == PrintExceptionListener) {
            GroovyMethodRunnerParams.instance.onExceptionOccured = new PrintExceptionColorListener()
        } else {
            log.info "Strange class name : ${occured.class.name}"
        }
        JdkLogFormatter.formatter = new JdkLogColorFormatter()

        ConsoleRedirect.setOutputWithRotationAndFormatter(outFile,maxDepth)
    }

    static void setConsoleColoringNoRedirect() {
        SetConsoleColoring.installAnsible()
        def occured = GroovyMethodRunnerParams.instance.onExceptionOccured;
        if (occured.class == PrintExceptionColorListener) {
        }else if (occured.class == PrintExceptionListener) {
            GroovyMethodRunnerParams.instance.onExceptionOccured = new PrintExceptionColorListener()
        } else {
            log.info "Strange class name : ${occured.class.name}"
        }
        JdkLogFormatter.formatter = new JdkLogColorFormatter()
        ConsoleRedirect.setOutputForConsleHandler(System.out)
        JdkLogFormatter.setLogFormatter()
    }


    static void installAnsible(){
        if(ansibleInstalled){
            log.info "already installed"
        }else{
            ansibleInstalled = true
            SetConsoleOut2.setConsoleOutIfNotInited()
            JrrClassUtils.setFieldValue(AnsiConsole,'installed',1000)
            SetConsoleOut2.proxyOut.setNestedOut AnsiConsole.wrapOutputStream(SetConsoleOut2.proxyOut.nestedOut)
            SetConsoleOut2.proxyErr.setNestedOut AnsiConsole.wrapOutputStream(SetConsoleOut2.proxyErr.nestedOut)
        }
    }

}
