package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.classpathhook.JavaClassPathHook
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.InitGeneral
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2CustomLogLayout
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2PatternLayout
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import org.apache.log4j.Level

import java.util.logging.Logger

@CompileStatic
class IdeaCommonInit implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static volatile boolean inInit = false;
    static volatile boolean inited = false;

    public static Log4j2CustomLogLayout proxyLogLayout = new Log4j2CustomLogLayout();
    public
    static List<String> ignoreClasses = ['java.', 'sun.', com.intellij.util.net.HttpConfigurable.getPackage().name, com.intellij.util.io.HttpRequests.name,];

    @Override
    void run() {
        init1()
    }

    static void init1() {
        log.info "in IdeaCommonInit init1 ${inited} ${inInit}"
        if (!inited && !inInit) {
            try {
                inInit = true;
                init1Impl()
            } catch (Throwable e) {
                log.info("${e}");
                e.printStackTrace();
                throw e;
            } finally {
                inInit = false;
            }
            inited = true;
        }

    }

    static void init1Impl() {
        SetConsoleOut2.setConsoleOutIfNotInited();
        log.info "in IdeaCommonInit.init1Impl"
        String ideaProxyLoggerName = "#com.intellij.util.proxy.CommonProxy";
        setIdeaLogLevel(ideaProxyLoggerName, Level.DEBUG)

        setIdeaLogLevel("#com.intellij.compiler.server.BuildManager", Level.WARN);

        //                setIdeaLogLevel(com.intellij.util.proxy.CommonProxy, Level.DEBUG)
//                setIdeaLogLevel(com.intellij.compiler.server.BuildManager, Level.WARN)

        Log4j2Utils.pl.isLogExceptionStackTrace = new IsLogExceptionStackTraceIdea();
        log.info "cp1"
        InitGeneral.init1()
        LogExitTimeHook.addShutDownHook()
        try {
            RedefineIdeaClassUtils.ideaLoggerTurnOff()
        } catch (NoSuchMethodException e) {
            log.info("method not found, seem idea is EE")
        }

        GeneralUtils.startLogTimer()
        JrrClassUtils.ignoreClassesForCurrentClass.add(com.intellij.util.proxy.CommonProxy.name)
        IdeaSetDependencyResolver3.setDepResolver()
        proxyLogLayout.additionalIgnore.addAll(ignoreClasses)
        Log4j2PatternLayout.customLayouts.put(ideaProxyLoggerName, proxyLogLayout)
        String javaHome = System.getProperty("java.home")

        log.info("java home : ${javaHome}")
        String tmpDir = System.getProperty("java.io.tmpdir")
        log.info("tmp dir : ${tmpDir}")
        if (SimpleJvmTiAgent.instrumentation == null) {
            log.info("jvm exception is null")
            JrrUtilities.showException("SimpleJvmTiAgent.instrumentation is null", new Exception("SimpleJvmTiAgent.instrumentation is null"))
        } else {
            JavaClassPathHook.installBothHooks()
            log.info "jvm hook redefined"
        }
        try {
            log.info "about to redefine jedi terminal"
            com.jpto.redefine.Terminal3Redefine.redefine3()
            log.info "jedi terminal redefined"
        } catch (Throwable e) {
            log.info("failed redefine terminal ${e}")
            JrrUtilities.showException("failed redefine terminal ${e}", e);
        }

    }

    static void setIdeaLogLevel(Class clazz, Level level) {
        setIdeaLogLevel("#${clazz.name}", level)
    }

    static void setIdeaLogLevel(String loggerName, Level level) {
        Log4j2Utils.setLogLevel(loggerName, level)
        com.intellij.openapi.diagnostic.Logger ll = com.intellij.openapi.diagnostic.Logger.getInstance(loggerName);
        ll.setLevel(level)
    }


}
