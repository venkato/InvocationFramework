package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

//TODO add debug option like : -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:1166,suspend=y,server=n
@CompileStatic
class JavaProcessRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static final int javaUsed = 2;
    public static final int javawUsed = 3;
    public static String javaBinaryDefault = 'java';
    public static List<String> javaArgsDefault = [];
    public static List<File> javaClasspathDefault = [];
    public static ClRef groovyMainRunner = new ClRef(groovy.ui.GroovyMain);

    Properties javaProps = new Properties();
    String javaBinary = javaBinaryDefault;
    List<String> javaArgs = javaArgsDefault;
    List<String> javaMainArgs = [];
    int xMxInMg = -1;
    ClRefRef mainClass;
    List<String> fullCmd = []
    File runDir;
    List<String> env = [];
    Process process;
    int exitCode;
    Date startTime;

    AddFileToClassloaderDummy javaClasspath = new AddFileToClassloaderDummy();

    JavaProcessRunner() {
        init()
    }

    void init() {
        javaClasspath.isLogFileAlreadyAdded = false
        if (javaClasspathDefault.size() > 0) {
            javaClasspath.addAll javaClasspathDefault
        }
        File javaExec = org.apache.commons.lang3.SystemUtils.getJavaHome().child('bin/java')
        javaBinaryDefault = javaExec.absolutePath
    }

    void buildCmd() {
        fullCmd.add javaBinary
        fullCmd.addAll javaArgs
        if (xMxInMg > 0) {
            String xmxAlreadyAdded = javaArgs.find { it.startsWith('-Xmx') }
            if (xmxAlreadyAdded != null) {
                throw new Exception("-Xmx was added in another way : ${xmxAlreadyAdded} ")
            }
            fullCmd.add("-Xmx${xMxInMg}m".toString())
        }
        fullCmd.addAll javaProps.collect { "-D${it.key}=${it.value}".toString() }
        if (javaClasspath.addedFiles2.size() > 0) {
            fullCmd.add '-classpath'
            String classPath3 = javaClasspath.addedFiles2.collect { it.absolutePath }.join(File.pathSeparator)
            fullCmd.add classPath3
        }
        fullCmd.add mainClass.clRef.className
        fullCmd.addAll javaMainArgs

    }

    void setHeapDumpPath(File pathToHeapDump){
        File parentFile = pathToHeapDump.getParentFile()
        if(!parentFile.exists()){
            throw new FileNotFoundException("heap dump parent path not found : ${parentFile}")
        }
        javaArgs.add "-XX:HeapDumpPath=${pathToHeapDump.getAbsolutePath()}".toString()
    }

    void addJmxOpts() {
        javaProps.setProperty('com.sun.management.jmxremote', 'true')
        javaProps.setProperty('com.sun.management.jmxremote.ssl', 'false')
        javaProps.setProperty('com.sun.management.jmxremote.authenticate', 'false')
    }

    void setJrrConfig2Dir(File dir) {
        assert dir.exists()
        javaProps.setProperty(JrrStarterConstatnts.jrrConfig2DirSystemProperty, dir.getAbsolutePath())
    }


    void addJmxOpts2(int port) {
        javaProps.setProperty('com.sun.management.jmxremote.port', "${port}")
        addJmxOpts()
    }

    void setJrrRunner2(int type) {
        if (SystemUtils.IS_OS_WINDOWS) {
            if (type in [javaUsed, javawUsed]) {

            } else {
                throw new IllegalStateException("Invalid type : ${type}, allowed : 2 or 3")
            }
        } else {
            type = javaUsed
        }
        File jrrStarterLibsDir = JrrStarterJarRefs.groovyClasspathDir.resolveToFile()
        setJrrRunner(type, jrrStarterLibsDir, JrrStarterJarRefs.groovyRunner.resolveToFile())
    }

    void setJrrRunner(int runnerType, File jrrStarterLibsDir, File groovyRunnerScript) {
        // for runnerType see
        new ClRef('net.sf.jremoterun.utilities.init.commonrunner.JrrRunnerProperties')
        assert groovyRunnerScript.isFile()
        File jrrFile = jrrStarterLibsDir.child(JrrStarterJarRefs2.jremoterun.getJarName())
        assert jrrFile.exists()
        javaArgs.add "-javaagent:${jrrFile.absolutePath}".toString()
        javaClasspath.addF jrrStarterLibsDir.child(JrrStarterJarRefs2.groovy_custom.getJarName())
        javaClasspath.addF jrrStarterLibsDir.child(JrrStarterJarRefs2.groovy.getJarName())
        mainClass = groovyMainRunner
        javaMainArgs.add(0, groovyRunnerScript.absolutePath)
        javaMainArgs.add(1, runnerType as String)
    }


    void runCmd() {
        String[] env2
        if (env.size() == 0) {
            env2 = null
        } else {
            env2 = env.toArray(new String[0])
        }
        startTime = new Date()
        String[] cmdArray = fullCmd.toArray(new String[0])
        process = Runtime.getRuntime().exec(cmdArray, env2, runDir)
    }

    void consomeOutAndWait() {
        process.consumeProcessOutput(System.out, System.err)
        exitCode = process.waitFor()
    }

    void checkExitCode() {
        if (exitCode != 0) {
            throw new Exception("Bad exit code : ${exitCode}")
        }
    }

    void buildAndRun() {
        buildCmd()
        runCmd()
        consomeOutAndWait()
        checkExitCode()
    }

}
