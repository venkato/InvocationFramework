package net.sf.jremoterun.utilities.nonjdk.classpath.console;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.st.GroovyRunnerConfigurator
import net.sf.jremoterun.utilities.groovystarter.st.MainClassInstanceSelector;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ConsoleClasses extends GroovyRunnerConfigurator implements MainClassInstanceSelector{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void doConfig() {

    }

    @Override
    Object getMainInstance() {
        String desc = "r : mavenDependenciesResolver"
        String firstParam3 = getFirstParam2(desc)
        switch (firstParam3){
            case 'r':
                removeFirstParam()
                MavenDependenciesResolver mavenDependenciesResolver =  MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
                return mavenDependenciesResolver
            default:
                throw new Exception("failed find shortcut : ${firstParam3}")
        }

    }
}
