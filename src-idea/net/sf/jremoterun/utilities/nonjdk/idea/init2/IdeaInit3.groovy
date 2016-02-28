package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassPathInit3

import java.util.logging.Logger

@CompileStatic
class IdeaInit3  extends InjectedCode  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    ClRef cnr1 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloaderSupport')

    ClRef init1 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init2.IdeaInit5')


    @Override
    Object get(Object key) {
        RunnableFactory.runRunner cnr1
        RunnableWithParamsFactory.fromClass4(init1,key)
        return null
    }

}
