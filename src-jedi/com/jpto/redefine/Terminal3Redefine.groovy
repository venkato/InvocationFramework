package com.jpto.redefine

import com.jediterm.terminal.ui.JediTermWidget
import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class Terminal3Redefine {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static boolean inited = false;


    static void redefine3(){

    }
    static void redefine4(){
        if(inited){
            log.info "already inited"
        }else {
            inited = true
            Class clazz = com.jediterm.terminal.ui.TerminalPanel
            log.info "${JrrUtils.getClassLocation(clazz)} ${clazz.classLoader}"
            CtClass ctClazz = JrrJavassistUtils.getClassFromDefaultPool(clazz)
            CtMethod method = JrrJavassistUtils.findMethod( clazz,ctClazz,"findHyperlink", 1);
//            log.info "${method.getParameterTypes()}"
            method.setBody """
{
     java.awt.Point p5 = panelToCharCoords(\$1);
    if (p5.x >= 0 && p5.x < myTerminalTextBuffer.getWidth() && p5.y <= myTerminalTextBuffer.getHeight()) {
      com.jediterm.terminal.TextStyle testStyle = myTerminalTextBuffer.getStyleAt(p5.x, p5.y);
      if (testStyle instanceof com.jediterm.terminal.HyperlinkStyle) {
        return (com.jediterm.terminal.HyperlinkStyle) testStyle;
      }
    }
    return null;
}
"""
            log.info "${clazz.name} redefined"
            JrrJavassistUtils.redefineClass(ctClazz, clazz)
        }
    }


}
