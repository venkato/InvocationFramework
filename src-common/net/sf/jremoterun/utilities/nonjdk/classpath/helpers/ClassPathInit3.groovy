package net.sf.jremoterun.utilities.nonjdk.classpath.helpers;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ClassNameReference
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.groovystarter.st.GroovyRunnerConfigurator
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.JavaVersionChecker
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ClassPathInit3  extends InjectedCode{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static ClRef ivyDepResolver = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.IvyDepResolverSetter')

    static ClRef gitRefSupport = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerSetter')

//    static  ClassLoader cl = JrrClassUtils.currentClassLoader;

    volatile static boolean inited = false

    @Override
    Object get(Object key) {
        List list = (List) key;
        if (list == null) {
            throw new IllegalArgumentException("need set adder and git base dir")
        }
        if (list.size() != 2) {
            throw new IllegalArgumentException("need set adder and git base dir, but got : ${list}")
        }
        AddFilesToClassLoaderCommon adder = list[0] as AddFilesToClassLoaderCommon;

        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }
        File gitDir = list[1] as File
        if (gitDir == null) {
            throw new IllegalArgumentException("Git dir is null")
        }
        addGitRefSupport(adder,gitDir)
        return null
    }

    @Deprecated
    static void setCommonStuff(AddFilesToClassLoaderCommon adder, File gitRepoBase){
        addGitRefSupport(adder,gitRepoBase)
    }

    static void addGitRefSupport(AddFilesToClassLoaderCommon adder, File gitRepoBase){
        if(inited){
            log.info "already inited"
        }else {
            JavaVersionChecker.checkJavaVersion();
            boolean logFileAlreadyAdded = adder.isLogFileAlreadyAdded
            adder.isLogFileAlreadyAdded = false
            adder.addAll DropshipClasspath.allLibsWithoutGroovy
            adder.isLogFileAlreadyAdded = logFileAlreadyAdded
            RunnableFactory.runRunner ivyDepResolver

            RunnableWithParamsFactory.fromClass4(gitRefSupport, [adder, gitRepoBase])
            if(InfocationFrameworkStructure.ifDir==null) {
                InfocationFrameworkStructure.ifDir = GitSomeRefs.ifFramework.resolveToFile()
            }
        }
    }
}
