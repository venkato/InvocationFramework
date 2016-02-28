package net.sf.jremoterun.utilities.nonjdk.consoleprograms;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodFinderException
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MethodNotFoundRunShell implements NewValueListener<GroovyMethodFinderException>,Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void newValue(GroovyMethodFinderException e) {
//        Binding binding = new Binding()
//        binding.setVariable("mc",GroovyMethodRunnerParams2.gmrp2.mainClassFound)
//        GroovyShellRunner.runConsole("mc.",binding);
        GroovyShellConsoleAux runner2 = new GroovyShellConsoleAux(e)
        runner2.binding.setVariable("gmrp",GroovyMethodRunnerParams.gmrp)
        runner2.binding.setVariable("gmrp2",GroovyMethodRunnerParams2.gmrp2)
        runner2.runConsole()
    }

    @Override
    void run() {
        GroovyMethodRunnerParams2.gmrp2.groovyMethodRunner2.methodNotFound = this
    }
}
