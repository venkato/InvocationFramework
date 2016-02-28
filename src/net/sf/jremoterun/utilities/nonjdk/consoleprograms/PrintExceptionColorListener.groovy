package net.sf.jremoterun.utilities.nonjdk.consoleprograms;

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.PrintExceptionListener
import org.fusesource.jansi.Ansi

import java.util.logging.Logger

@CompileStatic
public class PrintExceptionColorListener implements NewValueListener<Throwable> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile Ansi.Color exceptionColor = Ansi.Color.YELLOW;


    @Override
    public void newValue(Throwable t) {
        genericStuff(t)
        System.exit(1)
    }

    public static flushOutFile() {
        if (GroovyMethodRunnerParams.instance.fileOut != null) {
            try {
                GroovyMethodRunnerParams.instance.fileOut.flush()
            } catch (Throwable e2) {
            }
        }
    }


    void genericStuff(Throwable t) {
//        t.printStackTrace()
        t = JrrUtils.getRootException(t)
        StringBuilder sb2 = new StringBuilder()
        sb2.append(PrintExceptionListener.dumpPhase())
        String msg2=t.toString();
        Throwable cause = t.getCause()
        if (cause != null) {
            msg2 += "\n nested exception: ${cause}"
        }
        Ansi msg = Ansi.ansi().bg(exceptionColor).a(msg2).bg(Ansi.Color.DEFAULT).a('\n')
        sb2.append(msg);

        sb2.append(JrrClassUtils.printExceptionWithoutIgnoreClasses2(t).toString())
        System.err.println(sb2)
        flushOutFile()
    }
}
