package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.log.JavaCommonsLogger
import net.sf.jremoterun.utilities.nonjdk.log.Sl4jLogger
import org.apache.commons.logging.LogFactory
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class LoggigingRedefine {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    private static volatile boolean initDone = false;

    static void init() throws Exception {
        if (!initDone) {
            initDone = true;
            ClassRedefintions.init()
        }
    }


    public static void redifineLoggingGetLog() {
        try {
            init();
        } catch (Exception e1) {
            log.log(Level.WARNING, "", e1);
            return;
        }
        URL location1 = JrrUtils.getClassLocation(LogFactory)
        try {
            URL location = location1
            log.info "commons log location ${location}"
            redifineCommonsLoggingGetLog();
        } catch (Throwable e) {
            log.log(Level.WARNING, "failed redine commons LogFactory from ${location1}", e);
        }
        URL location = JrrUtils.getClassLocation(StaticLoggerBinder)
        log.info "sl4j log location ${location}"
        if (location != null && location.toString().contains("logback-classic")) {
            log.info "failed redefine sl4j logger : used logback ${location}"
        } else {
            try {
                redifineSl4jLoggingGetLog();
            } catch (Throwable e) {
                log.log(Level.WARNING, "failed redefine sl4j StaticLoggerBinder from ${location}", e);
            }
        }
    }

    public static void redifineCommonsLoggingGetLog() throws Exception {
        init();
        JavaCommonsLogger.setCommonsLoggerToLog4j2();
        Class class1 = LogFactory;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethod(class1, cc, "getFactory", 0);
        method.setBody """  
                return nullClassLoaderFactory;
            """;
        JrrJavassistUtils.redefineClass(cc, class1);
        LogFactory.getLog("test");
    }

    public static void redifineSl4jLoggingGetLog() throws Exception {
        Sl4jLogger.setSl4jLoggerToLog4j2();
        Class class1 = LoggerFactory;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethod(class1, cc, "getILoggerFactory", 0);
        method.setBody """
           return ${org.slf4j.impl.StaticLoggerBinder.name}.getSingleton().getLoggerFactory();

            """;
        JrrJavassistUtils.redefineClass(cc, class1);
        LoggerFactory.getLogger("test");
    }

}
