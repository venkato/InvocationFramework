package net.sf.jremoterun.utilities.nonjdk.javassist;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.junit.Test;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ClassRedefinitionTester implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Test
    @Override
    void run() {
        ClassRedefintions.redefineSunReflection()
        ClassRedefintions.redifineAccessibleObject()
        ClassRedefintions.redefineX509TrustManagerImpl()
        ClassRedefintions.redifinePackage()
        ClassRedefintions.redifineUrlClassLoader()
        ClassRedefintions.redifineSecurityManager()
        ClassRedefintions.redifineSocketClass()
        ClassRedefintions.redifineHttpsCertificateCheck1()
        ClassRedefintions.redifineClassLoader()
        ClassRedefintions.redefindeDnsResolving()

        LoggigingRedefine.redifineCommonsLoggingGetLog()
//        LoggigingRedefine.redifineSl4jLoggingGetLog()

    }
}
