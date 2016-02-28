package net.sf.jremoterun.utilities.nonjdk.shell.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.GroovyRunnerConfigurator
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class GroovyShellRunnerFromConsole extends GroovyRunnerConfigurator implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = GroovyMavenIds.all +//
            [GroovyMavenIds.groovyAll, LatestMavenIds.jline2,LatestMavenIds.commonsNet]

    public static ClRef cnr = new ClRef("net.sf.jremoterun.utilities.nonjdk.shell.console.ConRunner3")

    @Override
    void doConfig() {
        AddFilesToUrlClassLoaderGroovy adder = gmrp.addFilesToClassLoader
        addClassthapAndRun(adder)
    }

    void addClassthapAndRun(AddFilesToUrlClassLoaderGroovy adder) {
        adder.addAll mavenIds
        RunnableFactory.runRunner cnr
    }
}
