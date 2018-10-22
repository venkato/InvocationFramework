package net.sf.jremoterun.utilities.nonjdk.idea.classpathtester

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper2
import net.sf.jremoterun.utilities.nonjdk.groovy.ExtentionMethodChecker2
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollector
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorI


import java.util.logging.Logger;

@CompileStatic
class IdeaClassPathRuntimeTester{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassPathTesterHelper2 helper

    IdeaClassPathRuntimeTester(ClassPathTesterHelper2 helper) {
        this.helper = helper
    }

    IdeaClassPathRuntimeTester(ProblemCollectorI problemCollector) {
        helper = new ClassPathTesterHelper2(problemCollector)
    }


    public ClRef ideaBaseClassloader = new ClRef('com.intellij.util.lang.UrlClassLoader')
    public ClRef ideaBaseClassloader20211 = new ClRef('com.intellij.util.lang.PathClassLoader')


    static void runChecks(){
        ProblemCollector problemCollector = new ProblemCollector()
        IdeaClassPathRuntimeTester tester = new IdeaClassPathRuntimeTester(problemCollector)
        tester.checker()
        boolean allGood = problemCollector.checkIfProblemExistAndShowException()
        try {
            ExtentionMethodChecker2.check()
        }catch(Throwable e){
            log.info ("ExtentionMethodChecker2 failed ",e)
            JrrUtilities.showException("ExtentionMethodChecker2 failed ",e)
        }
        log.info "all good ? : ${allGood}"
    }

    void checker() {
        ClassLoader classLoaderParent = JrrClassUtils.getCurrentClassLoader().getClass().getClassLoader()
        String className1 = classLoaderParent.getClass().getName()
        if (className1 != ideaBaseClassloader.className && className1 != ideaBaseClassloader20211.className) {
            helper.addProblem(null, "Strange classloader : ${classLoaderParent}")
        } else {
            checkImpl()
        }
    }

    void checkImpl() {
        PluginClassLoader currentClassLoader = IdeaClasspathAdd.pluginClassLoader

        helper.checkNotSameClassLoader5(new ClRef('org.apache.log4j.Logger'), currentClassLoader)
        helper.checkNotSameClassLoader5(new ClRef('org.apache.commons.logging.LogFactory'), currentClassLoader)
        helper.checkNotSameClassLoader5(new ClRef('org.slf4j.LoggerFactory'), currentClassLoader)
//        helper.checkNotSameClassLoader5(new ClRef('javassist.ClassPath'), currentClassLoader)
        helper.checkNotSameClassLoader5(new ClRef('com.sun.jna.Callback'), currentClassLoader)
        helper.checkNotSameClassLoader5(GroovyObject, currentClassLoader)
        helper.checkNotSameClassLoader5(new ClRef('sun.jvmstat.monitor.HostIdentifier'), currentClassLoader)
        helper.checkNotSameClassLoader5(new ClRef('org.codehaus.groovy.runtime.DefaultGroovyMethods'), currentClassLoader)

    }

}
