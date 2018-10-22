package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.DefaultClasspathAdder
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassPathInit3
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

import java.util.logging.Logger

@CompileStatic
class IdeaInit5 extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static ClRef init6 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init2.IdeaInit6')

    static ClRef init4 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init2.IdeaInit4')


    @Override
    Object get(Object key) {
        initImpl(key)
        return null
    }

    void initImpl(Object o) {
        List list = o as List
        File gitBaseDir = list.get(0)
        assert gitBaseDir.exists()
        File logDir = list.get(1)
        if(logDir==null){
            throw new NullPointerException('logDir is null')
        }
        assert logDir.isDirectory()
        initImpl2(gitBaseDir,logDir)
    }

    static void initImpl2(File gitBaseDir,File logDir){
        IdeaLogRedirect.doLogOutRedirect(logDir)
        ClassPathInit3.addGitRefSupport(IdeaClasspathAdd.addCl,gitBaseDir)
        DefaultClasspathAdder.addRefs(IdeaClasspathAdd.addCl)
        RunnableFactory.runRunner init6
        RunnableFactory.runRunner init4
    }



}
