package net.sf.jremoterun.utilities.javassist

import groovy.transform.CompileStatic
import javassist.*
import javassist.runtime.Desc
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.*

@Deprecated
@CompileStatic
public class ClassRedefintions {

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());


    private static volatile boolean initDone = false;

    static void init() throws Exception {
        if (!initDone) {
            initDone = true;
            ClassPool.doPruning = false;
            Desc.useContextClassLoader = true;
            final ClassPool pool = ClassPool.getDefault();
            final ClassPath classPath = new ClassClassPath(JrrClassUtils.getCurrentClass());
            pool.appendClassPath(classPath);
        }
    }


    public static void redifineSecurityManager() throws Exception {
        init();
        Class clazz = java.lang.System
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "setSecurityManager", 1);

        method1.setBody("{setSecurityManager0(null);}");
        JrrJavassistUtils.redefineClass(cc, clazz);
    }


    static void redifineSocketClass() throws Exception {
        init();
        Class clazz = Socket
        final CtClass cc = getClassFromDefaultPool(clazz);
        CtBehavior intiMethod = JrrJavassistUtils.findConstructor(cc, 2)
        intiMethod.insertBefore("\$2=446;");
        final CtBehavior connectMethod = findMethod(clazz, cc, "connect", 2);
        connectMethod.insertBefore("""
                ${createLogVar}
                ${LogVarName2}.info(\$1.toString());
            """);
        connectMethod.insertAfter("""
                ${createLogVar}
                ${LogVarName2}.info(\$1.toString()+" connect ok");
            """)
        JrrJavassistUtils.redefineClass(cc, clazz);
    }


    static void redifineHttpsCertificateCheck1() throws Exception {
        init();
        Class class1 = JrrUtilities.getCurrentClassLoader().loadClass("sun.net.www.protocol.https.HttpsClient");
        final CtClass cc = getClassFromDefaultPool(class1);
        final CtMethod method = cc.getDeclaredMethod("checkURLSpoofing");
        method.setBody("{}");
        JrrJavassistUtils.redefineClass(cc, class1);
    }


    static void redifineClassLoader() throws Exception {
        init();
        Class class1 = ClassLoader
        final CtClass cc = getClassFromDefaultPool(class1);
        CtBehavior method1;
        try {
            method1 = findMethod(class1, cc, "checkCerts", 2);
        } catch (NoSuchMethodException e) {
            log.info("failed find checkCerts method ${e}");
            return;
        }
        method1.setBody("{}");
        redefineClass(cc, class1);
    }


}
