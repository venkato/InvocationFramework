package net.sf.jremoterun.utilities.nonjdk.idea;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ClassNameReference
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrIdeaInitializer1 extends GroovyRunnerConfigurator2  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static ClRef cnr = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.JrrIdeaInitializer2')

    @Override
    void doConfig() {
        log.info "cp1"
//        AddJrrLibToCommonIdeaClassloader2.addJrrLibToCommonIdeaClassloader3();
//        IdeaRedefineClassloader.redifineClassloader3()
//        IdeaCommonInit.init1()
        RunnableFactory.runRunner cnr
    }
}
