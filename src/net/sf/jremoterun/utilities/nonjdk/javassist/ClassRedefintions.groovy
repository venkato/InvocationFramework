package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.ClassClassPath
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtMethod
import javassist.runtime.Desc
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.LogVarName2
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.createLogVar
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.findMethod
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.getClassFromDefaultPool
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.redefineClass

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


    static void redifinePackage() throws Exception {
        init();
        Class clazz = java.lang.Package
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        if (true) {
            CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "isSealed", 1);
            method1.setBody("{return false;}");
        }
        if (true) {
            CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "isSealed", 0);
            method1.setBody("{return false;}");
        }
        JrrJavassistUtils.redefineClass(cc, clazz);
    }

    static void redifineUrlClassLoader() throws Exception {
        init();
        Class clazz = URLClassLoader
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "isSealed", 2);

        method1.setBody("{return false;}");
        JrrJavassistUtils.redefineClass(cc, clazz);
    }


    static void redifineSecurityManager() throws Exception {
        init();
        Class clazz = java.lang.System
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "setSecurityManager", 1);

        method1.setBody("{setSecurityManager0(null);}");
        JrrJavassistUtils.redefineClass(cc, clazz);
    }

    static void redifineAccessibleObject() throws Exception {
        init();
        Class clazz = java.lang.reflect.AccessibleObject
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "slowCheckMemberAccess", 5);
        method1.setBody("{}");
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
        ClRef cr = new ClRef('sun.net.www.protocol.https.HttpsClient')
        Class class1 = cr.loadClass(JrrUtilities.getCurrentClassLoader());
        final CtClass cc = getClassFromDefaultPool(class1);
        final CtMethod method = cc.getDeclaredMethod("checkURLSpoofing");
        method.setBody("{}");
        JrrJavassistUtils.redefineClass(cc, class1);
    }


    static void redefineX509TrustManagerImpl() throws Exception {
        init();
        ClRef cr = new ClRef('sun.security.ssl.X509TrustManagerImpl')
        Class class1 = cr.loadClass(JrrUtilities.getCurrentClassLoader());

        final CtClass cc = getClassFromDefaultPool(class1);
        final Collection<CtMethod> methods = cc.getDeclaredMethods().findAll { it.name == 'checkTrusted' };
        assert methods.size() == 2
        methods.each { it.setBody("{}") };
        JrrJavassistUtils.redefineClass(cc, class1);
    }

    static void redefineSunReflection() throws Exception {
        init();
        ClRef cr = new ClRef('sun.reflect.Reflection')
        Class class1 = sun.reflect.Reflection;//cr.loadClass(JrrUtilities.getCurrentClassLoader());

        final CtClass cc = getClassFromDefaultPool(class1);
        final CtMethod method1 = JrrJavassistUtils.findMethod(class1,cc,'ensureMemberAccess',4)
        method1.setBody("{}")
        final CtMethod method2 = JrrJavassistUtils.findMethod(class1,cc,'verifyMemberAccess',4)
        method2.setBody("{return true;}")

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


    static void redefindeDnsResolving() {
        init();
        Class class1 = InetAddress;
        final CtClass cc = getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethod(class1, cc, "getAllByName", 2);
        method.insertBefore """
if(\$1!=null && \$1.indexOf('.')==-1  && \$1.length() >1 ){
    if( \$1.toUpperCase().startsWith("myhost")){
        \$1 = "myhost.fullname";    
    }else{
        if(!"localhost".equals(\$1.toLowerCase())){
           \$1 = \$1+".suffix";
        }
    }
}
""";
        JrrJavassistUtils.redefineClass(cc, class1);
    }


}
