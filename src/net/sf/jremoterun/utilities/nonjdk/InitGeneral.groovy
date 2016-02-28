package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.consoleprograms.SetConsoleColoring
import net.sf.jremoterun.utilities.nonjdk.javassist.LoggigingRedefine
import net.sf.jremoterun.utilities.nonjdk.log.JdkLog2Log4jInit
import net.sf.jremoterun.utilities.nonjdk.log.Log4j1Utils
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import net.sf.jremoterun.utilities.nonjdk.settings.JrrUtilitiesSettings
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.fife.rsta.ac.java.SourceCompletionProvider

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class InitGeneral {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean inInit = false;
    public static volatile boolean inited = false;
    public static volatile boolean checkJavassistClassloader = false;
    public static volatile boolean useColorOutput = true;
    public static volatile ClRef helfyInit = new ClRef('net.sf.jremoterun.utilities.nonjdk.helfyutils.HelpfyRegister')
    public static volatile ClRef helfyCore = new ClRef('one.helfy.JVM')

    static void init1() {
        if (!inited && !inInit) {
            try {
                inInit = true;
                init1impl()
            } catch (Exception e) {
                log.info("${e}");
                JrrUtilities.showException("failed init", e)
                throw e;
            } finally {
                inInit = false;
            }
            inited = true;
        }
    }

    static void checkJavassistClassloaderCorrectly() {
        String className = javassist.runtime.Desc.name
        ClassLoader javassistClassLoader = JrrClassUtils.currentClassLoader.loadClass(className).classLoader
        if (javassistClassLoader != null) {
            JrrUtilities.showException("javassist ClassLoader", new Exception("Class  ${className} loaded by not boot classloader : ${javassistClassLoader}"))
        }
    }

    static void init1impl() {
        if (useColorOutput) {
            SetConsoleColoring.installAnsible()
        }
        if (checkJavassistClassloader) {
            checkJavassistClassloaderCorrectly();
        }
//                ProxySelectorLogger.setProxySelectorWithJustLogging();
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j1Utils.getPackage().getName());
        SettingsChecker.showAsSwing = true
        try {
            Log4jConfigurator.registerLog4jConfiguratorAndMbeans()
        } catch (Exception e) {
            Throwable e2 = JrrUtils.getRootException(e);
            if (e2 instanceof ClassNotFoundException && e2.message == 'org.apache.log4j.jmx.HierarchyDynamicMBean') {
            } else {
                log.log(Level.WARNING, "failed regiter log4j", e2)
            }
        }
        Log4j1Utils.setLog4jAppender()
        Log4j2Utils.setLog4jAppender()
        JdkLog2Log4jInit.jdk2log4j();
        CheckNonCache2.check();
        CheckNonCache2.check(JrrUtilities);

        LoggigingRedefine.redifineLoggingGetLog();
        SimpleUncaughtExceptionHandler.setDefaultUncaughtExceptionHandler()
        GeneralUtils.startLogTimer()
        int pid = PidDetector.detectPid();
        log.info "pid = ${pid}"
//                if (MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null) {
        IvyDepResolver2.setDepResolver()
//                }
        SourceCompletionProvider.loadPrivateMemberAlways = true
        SharedObjectsUtils.getClassLoaders().put(JrrUtilitiesSettings.generalInitCLassLoaderId, InitGeneral.classLoader);
        if (SimpleFindParentClassLoader.getDefaultClassLoader() == ClassLoader.getSystemClassLoader()) {
            SimpleFindParentClassLoader.setDefaultClassLoader(InitGeneral.classLoader)
        }
        checkMavenUrlInDepResolver()
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            try {
                helfyCore.loadClass2()
                RunnableFactory.runRunner helfyInit
            } catch (ClassNotFoundException e) {
                log.info("failed load class ${helfyCore} ${e}")
            }
        }
    }

    static void checkMavenUrlInDepResolver() {
        MavenDefaultSettings mds = MavenDefaultSettings.mavenDefaultSettings;
        MavenDependenciesResolver resolver = mds.mavenDependenciesResolver;
        if (resolver == null) {
            String msg = "mavenDependenciesResolver undefined"
            JrrUtilities.showException(msg, new Exception("${msg}"))
        } else {
            String urlInDepResolver = resolver.getMavenRepoUrl().toString()
            if (!urlInDepResolver.endsWith('/')) {
                urlInDepResolver += '/'
            }
            String urlExpected = mds.mavenServer;
            if (!urlExpected.endsWith('/')) {
                urlExpected += '/'
            }
            if (urlInDepResolver != urlExpected) {
                String msg = "Wrong maven server in dep resolver : ${urlInDepResolver}"
                JrrUtilities.showException(msg, new Exception("${msg} , expected = ${urlExpected}"))
            }
        }

    }


}
