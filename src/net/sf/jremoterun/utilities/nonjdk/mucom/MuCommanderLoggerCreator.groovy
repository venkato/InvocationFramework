package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils

import java.util.logging.Logger

@CompileStatic
class MuCommanderLoggerCreator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static ClRef cnr1 = new ClRef('com.mucommander.RuntimeConstants')
    public static ClRef cnr2 = new ClRef('com.mucommander.utils.MuLogging')

    static void createAndDefine() {
        CtClass clazz = create();
        ClassLoader classLoader = JrrClassUtils.currentClassLoader.loadClass(cnr1.className).classLoader
        clazz.toClass(classLoader, null)
    }

    static CtClass create() {
        JrrJavassistUtils.init()
        def classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(cnr2.className)
        CtMethod ctMethod = CtNewMethod.make("""
public static void configureLogging() {
}
""", ctClass)
        ctClass.addMethod(ctMethod)
        return ctClass;

    }

}
