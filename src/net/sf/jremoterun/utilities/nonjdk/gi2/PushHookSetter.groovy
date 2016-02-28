package net.sf.jremoterun.utilities.nonjdk.gi2

import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.CodeInjector
import org.junit.Test;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class PushHookSetter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Test
    void t1(){
        buildPushHook2("dsf")
//        buildCommitHook()
    }



    static void buildPushHook2(String enableUrl) throws Exception {
        GitPushHook.enabledUrlPush = enableUrl
        CodeInjector.putInector2(org.eclipse.jgit.transport.PushProcess,new GitPushHook())
        buildPushHook()
    }

    static void buildPushHook() throws Exception {
        Class  class1 = org.eclipse.jgit.transport.PushProcess
        log.info("classloader : "+class1.getClassLoader());
        CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        CtBehavior method = JrrJavassistUtils.findConstructor(cc, 3);
        method.insertBefore """
            ${CodeInjector.createSharedObjectsHookVar2(class1)}
            ${CodeInjector.myHookVar}.get(\$1);
        """
        JrrJavassistUtils.redefineClass(cc,class1)
    }

    /**
     * Does work ?
     */
    static void buildCommitHook(){
        Class  class1 = org.eclipse.jgit.api.GitCommand
        log.info("classloader : "+class1.getClassLoader());
        CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1)
        CtBehavior method = JrrJavassistUtils.findMethod(class1,cc,"checkCallable",0);;
        method.insertBefore """
            ${CodeInjector.createSharedObjectsHookVar2(class1)}
            ${CodeInjector.myHookVar}.get(this);
            
        """
        JrrJavassistUtils.redefineClass(cc,class1)
    }

}
