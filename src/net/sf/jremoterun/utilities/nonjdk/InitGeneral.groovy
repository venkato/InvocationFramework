package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.infonode.properties.propertymap.JrrIdwPropertyMapManager
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.compile.auxh.AddGroovyToParentClResolver
import net.sf.jremoterun.utilities.nonjdk.consoleprograms.SetConsoleColoring
import net.sf.jremoterun.utilities.nonjdk.ivy.JrrDependecyAmenderDefault
import net.sf.jremoterun.utilities.nonjdk.javassist.LoggigingRedefine
import net.sf.jremoterun.utilities.nonjdk.log.AddDefaultIgnoreClasses
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
    public static volatile InitGeneral initGeneral1 = new InitGeneral();

    static void init1() {
        if (!inited && !inInit) {
            try {
                InitLogTracker.defaultTracker.addLog("start init at ${new Date()}");
                inInit = true;
                initGeneral1.init1impl()
                InitLogTracker.defaultTracker.addLog("finished init at ${new Date()}");
            } catch (Throwable e) {
//                JrrUtilities.showException("failed init", e)
                InitLogTracker.defaultTracker.addException("failed init",e);
                log.info("${e}");
                throw e;
            } finally {
                inInit = false;
            }
            inited = true;
        }
    }

    static void checkJavassistClassloaderCorrectly() {
        String className = javassist.runtime.Desc.getName()
        ClassLoader javassistClassLoader = JrrClassUtils.currentClassLoader.loadClass(className).classLoader
        if (javassistClassLoader != null) {
            JrrUtilities.showException("javassist ClassLoader", new Exception("Class  ${className} loaded by not boot classloader : ${javassistClassLoader}"))
        }
    }

    void init1impl() {
        firstAction();
        InitLogTracker.defaultTracker.addLog("initing coloring")
        Java11ModuleSetDisable.doIfNeeded();
        InitLogTracker.defaultTracker.addLog("java11 module check disabled")
        if (useColorOutput) {
            SetConsoleColoring.installAnsible()
        }
        if (checkJavassistClassloader) {
            checkJavassistClassloaderCorrectly();
        }
//                ProxySelectorLogger.setProxySelectorWithJustLogging();
        AddDefaultIgnoreClasses.addIgnoreClasses()
//        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j1Utils.getPackage().getName());
        SettingsChecker.showAsSwing = true
        InitLogTracker.defaultTracker.addLog("setting log4j configurator")
        try {
            Log4jConfigurator.registerLog4jConfiguratorAndMbeans()
        } catch (Exception e) {
            InitLogTracker.defaultTracker.addException("failed log4j mbean register",e)
            Throwable e2 = JrrUtils.getRootException(e);
            if (e2 instanceof ClassNotFoundException && e2.getMessage() == 'org.apache.log4j.jmx.HierarchyDynamicMBean') {
            } else {
                log.log(Level.WARNING, "failed regiter log4j", e2)
            }
        }
        InitLogTracker.defaultTracker.addLog("setting logging")
        setLogging()
        InitLogTracker.defaultTracker.addLog("exception handler setting");
        SimpleUncaughtExceptionHandler.setDefaultUncaughtExceptionHandler()
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            if(SimpleJvmTiAgent.instrumentation != null) {
                net.sf.jremoterun.utilities.nonjdk.serviceloader.ServiceLoaderStorage.init()
            }
        }
        int pid = PidDetector.detectPid();
        log.info "pid = ${pid}"
//                if (MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null) {
        InitLogTracker.defaultTracker.addLog("setting resolver");
        JrrDependecyAmenderDefault.setResolverAmender();
        net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl.setManyRepoLoader();
        net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler.setHandler()
        //IvyDepResolver2.setDepResolver()
//                }
        //SourceCompletionProvider.loadPrivateMemberAlways = true
        InitLogTracker.defaultTracker.addLog("setting default classloader for remote code execution");
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
        afterDependencySet()
        initifFrameworkDir()
        InitLogTracker.defaultTracker.addLog("init general done fine");
        lastAction()
    }

    void initifFrameworkDir(){
        if(InfocationFrameworkStructure.ifDir == null) {
            InfocationFrameworkStructure.ifDir = GitSomeRefs.ifFramework.resolveToFile()
        }
    }

    void firstAction(){

    }

    void setLogging(){
        InitLogTracker.defaultTracker.addLog("setting log4j2 appender")
        Log4j2Utils.setLog4jAppender()
        InitLogTracker.defaultTracker.addLog("setting log4j1 appender")
        Log4j1Utils.setLog4jAppender()
        InitLogTracker.defaultTracker.addLog("setting jdk log appender")
        JdkLog2Log4jInit.jdk2log4j();
        CheckNonCache2.check();
        CheckNonCache2.check(JrrUtilities);

        LoggigingRedefine.redifineLoggingGetLog();
        GeneralUtils.startLogTimer()
        InitLogTracker.defaultTracker.addLog("logging set fine");
    }

    void afterDependencySet(){
        AddGroovyToParentClResolver.setRef();
    }

    void lastAction(){

    }

    void checkMavenUrlInDepResolver() {
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
