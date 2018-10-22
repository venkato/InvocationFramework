package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils;

import java.util.logging.Logger;

@CompileStatic
class IdeaProxyDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void disableIdeaProxy(){
        Class cl = com.intellij.util.proxy.CommonProxy
        CtClass pool = JrrJavassistUtils.getClassFromDefaultPool(cl)
        CtMethod method = JrrJavassistUtils.findMethod(cl, pool, 'isInstalledAssertion', 0)
        method.setBody(null)
        JrrJavassistUtils.redefineClass(pool,cl)
    }
}
