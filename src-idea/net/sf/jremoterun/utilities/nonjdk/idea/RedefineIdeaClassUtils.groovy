package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.util.registry.RegistryValue
import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils

import java.util.logging.Logger


/**
 * Created by nick on 02.01.2017.
 */
@CompileStatic
class RedefineIdeaClassUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static void ideaLoggerTurnOff() throws Exception {
        log.info("ideaLoggerTurnOff enter")
        Class class1 = com.intellij.idea.IdeaLogger;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        CtBehavior invokeMethod = JrrJavassistUtils.findMethod(class1, cc, "logErrorHeader", 1);
        invokeMethod.setBody('{myLogger.info("Last Action Id = " + ourLastActionId ); }');
        JrrJavassistUtils.redefineClass(cc, class1);
        log.info("ideaLoggerTurnOff exit")
    }




    public static void setDisalogNotBlocking() throws Exception {
        RegistryValue registryValue = Registry.get("ide.perProjectModality");

        JrrClassUtils.setFieldValue(registryValue, "myBooleanCachedValue", true);
        Class class1 = com.intellij.openapi.ui.DialogWrapper.IdeModalityType;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        CtBehavior invokeMethod = JrrJavassistUtils.findMethod(class1, cc, "toAwtModality", 0);
        invokeMethod.setBody("{return java.awt.Dialog.ModalityType.MODELESS;  }");
        JrrJavassistUtils.redefineClass(cc, class1);
    }
}
