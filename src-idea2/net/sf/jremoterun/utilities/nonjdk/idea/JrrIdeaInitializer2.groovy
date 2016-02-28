package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2

import java.util.logging.Logger

@CompileStatic
class JrrIdeaInitializer2 implements Runnable  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        log.info "cp1"
        IdeaCommonInit.init1()
    }
}
