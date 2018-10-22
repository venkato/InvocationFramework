package net.sf.jremoterun.utilities.nonjdk.shell.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.groovystarter.st.GroovyRunnerConfigurator
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class GroovyShellRunnerFromConsoleWithMap  implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef cnr2 = new ClRef("net.sf.jremoterun.utilities.nonjdk.shell.console.ConRunner3WithArgs")

    public Map shortcuts

    GroovyShellRunnerFromConsoleWithMap(Map shortcuts) {
        this.shortcuts = shortcuts
    }

    @Override
    void run() {
        AddFilesToUrlClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrp.addFilesToClassLoader
        addClassPathAndRun(adder)
    }

    void addClassPathAndRun(AddFilesToUrlClassLoaderGroovy adder) {
        adder.addAll GroovyShellRunnerFromConsole.mavenIds
        RunnableWithParamsFactory.fromClass4(cnr2, shortcuts)
    }

}
