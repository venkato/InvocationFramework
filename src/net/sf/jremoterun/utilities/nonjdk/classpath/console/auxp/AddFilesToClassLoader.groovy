package net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.methodrunner.AuxMethodRunner;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AddFilesToClassLoader implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        AddFilesToUrlClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrp.addFilesToClassLoader
        new AuxMethodRunner().invokeMethod(adder)
    }
}
