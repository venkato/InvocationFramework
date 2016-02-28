package net.sf.jremoterun.utilities.nonjdk.javassist

import javassist.CtClass
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.CodeInjector
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class SystemExitListener  extends InjectedCode{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static NewValueListener<Integer> systemExitListener2


    static void setSystemExistListener(NewValueListener<Integer> systemExitListener){
        assert systemExitListener!=null;
        assert systemExitListener2 ==null
        systemExitListener2 = systemExitListener;

        Class clazz = java.lang.Shutdown
        CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz)
        CtMethod method = JrrJavassistUtils.findMethod(clazz, ctClass, 'exit', 1)
        method.insertBefore """
            ${CodeInjector.createSharedObjectsHookVar2(clazz)}
            ${CodeInjector.myHookVar}.get(new java.lang.Integer(\$1));
        """
        JrrJavassistUtils.redefineClass(ctClass, clazz);
        CodeInjector.putInector2(clazz, new SystemExitListener())
    }

    @Override
    Object getImpl(Object key) throws Exception {
        int exitCode = key as int
        systemExitListener2.newValue(exitCode)
        return null;
    }
}
