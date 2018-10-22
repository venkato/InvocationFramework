package net.sf.jremoterun.utilities.nonjdk.redefineclass

import groovy.transform.CompileStatic
import net.sf.jremoterun.FindParentClassLoader
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils

import javax.management.MalformedObjectNameException
import javax.management.ObjectName
import java.lang.instrument.ClassDefinition;
import java.util.logging.Logger;

@CompileStatic
class RedefineClassImpl implements RedefineClassI, DefaultObjectName {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClassLoader defaultClassLoader = JrrClassUtils.getCurrentClassLoader();

    public static RedefineClassImpl defaultInstance = new RedefineClassImpl();

    static void registerMbeanIfNeeded() {
        if(JrrUtils.findLocalMBeanServer().isRegistered(objectName)){
            log.info "already registered ${objectName}"
        }else{
            registerMbean()
        }
    }

    static void registerMbean() {
        MBeanFromJavaBean.registerMBean(new RedefineClassImpl());
    }


    static void redefineClassOnlyS(Class clazz, byte[] bytes) {
        final ClassDefinition classDefinition = new ClassDefinition(clazz, bytes);
        ClassDefinition[] classDefinitions = [classDefinition];
        if (SimpleJvmTiAgent.instrumentation == null) {
            throw new RuntimeException("JVM ti agent is not initialized");
        }
        SimpleJvmTiAgent.instrumentation.redefineClasses(classDefinitions);
        log.info "class redefined : ${clazz.getName()}"
    }

    ClassLoader findClassloader(String classLoaderId) {
        if (classLoaderId == thisClassCl) {
            return defaultClassLoader
        }
        FindParentClassLoader loader = SharedObjectsUtils.getFindParentClassLoader()
        return loader.findClassLoader(classLoaderId)
    }

    @Override
    void redefineClassOnly(String className, byte[] bytes, String classloaderId) {
        Class clazz = findClassloader(classloaderId).loadClass(className);
        redefineClassOnlyS(clazz, bytes)
    }

    @Override
    void redefineClassOnly(String className, String classloaderId) {
        Class clazz = findClassloader(classloaderId).loadClass(className);
        Class[] clll = [clazz]
        SimpleJvmTiAgent.redefineClasses(clll);
        log.info "class reloaded : ${className}"
    }

    @Override
    void redefineClassAndAnonClasses(String className, String classloaderId) {
        Class clazz = findClassloader(classloaderId).loadClass(className);
        JrrJavassistUtils.reloadClassAndAnonClasses(clazz);
    }

    @Override
    ObjectName getDefaultObjectName() throws MalformedObjectNameException {
        return objectName;
    }
}
