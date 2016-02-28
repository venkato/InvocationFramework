package net.sf.jremoterun.utilities.nonjdk.shell.core

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import org.codehaus.groovy.tools.shell.Groovysh
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.InteractiveShellRunner
import org.codehaus.groovy.tools.shell.Interpreter

import java.util.logging.Logger


// https://github.com/Dispader/groovysh-command-grab
@CompileStatic
class GroovyshAux extends Groovysh {

    private static final Logger log2 = JrrClassUtils.getJdkLogForCurrentClass();

    public Runnable afterInit

    public Binding binding6

    public NewValueListener<Throwable> newEx


    GroovyshAux(ClassLoader classLoader, final Binding binding, final IO io, final Closure registrar) {
        super(classLoader, binding, io, registrar)
        binding6 = binding
    }

    @Override
    void displayWelcomeBanner(InteractiveShellRunner runner) {
        super.displayWelcomeBanner(runner)
        if (afterInit != null) {
            afterInit.run()
        }
    }

    // below method copied from parent class
    void displayError(final Throwable cause) {
        if(newEx==null){
            cause.printStackTrace()
        }else {
            newEx.newValue(cause)
        }
    }



}
