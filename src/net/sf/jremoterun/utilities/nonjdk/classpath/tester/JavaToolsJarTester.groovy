package net.sf.jremoterun.utilities.nonjdk.classpath.tester


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import sun.jvmstat.monitor.HostIdentifier

import java.util.logging.Logger

@CompileStatic
class JavaToolsJarTester {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassPathTesterHelper2 helper

    JavaToolsJarTester(ClassPathTesterHelper2 helper) {
        this.helper = helper
    }

    void checkToolsJar() {
//        Class hostId =
        if(HostIdentifier.classLoader != JavaToolsJarTester.classLoader){
            helper.addProblem(HostIdentifier.name,"Class from stange classloader : ${HostIdentifier.classLoader}")
        }
        helper.checkClassOnce5(HostIdentifier, ClassPathTesterHelper2.mavenCommonUtils.getToolsJarFile())
    }


}