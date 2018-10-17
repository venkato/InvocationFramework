package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
class JavaProcessRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String javaBinaryDefault = 'java'
    public static List<String> javaArgsDefault = []
    public static List<File> javaClasspathDefault = []
    public static ClRef groovyMainRunner = new ClRef(groovy.ui.GroovyMain)

    Properties javaProps = new Properties()
    String javaBinary = javaBinaryDefault
    List<String> javaArgs = javaArgsDefault
    List<String> javaMainArgs = []
    ClRefRef mainClass
    List<String> fullCmd = []
    File runDir;
    List<String> env = [];
    Process process
    int exitCode
    Date startTime

    AddFileToClassloaderDummy javaClasspath = new AddFileToClassloaderDummy();

    JavaProcessRunner() {
        init()
    }

    void init(){
        javaClasspath.isLogFileAlreadyAdded = false
        if(javaClasspathDefault.size()>0) {
            javaClasspath.addAll javaClasspathDefault
        }
        File javaExec = org.apache.commons.lang3.SystemUtils.getJavaHome().child('bin/java')
        javaBinaryDefault = javaExec.absolutePath
    }

    void buildCmd() {
        fullCmd.add javaBinary
        fullCmd.addAll javaArgs
        fullCmd.addAll javaProps.collect { "-D${it.key}=${it.value}".toString() }
        if (javaClasspath.addedFiles2.size() > 0) {
            fullCmd.add '-classpath'
            String classPath3 = javaClasspath.addedFiles2.collect { it.absolutePath }.join(File.pathSeparator)
            fullCmd.add classPath3
        }
        fullCmd.add mainClass.clRef.className
        fullCmd.addAll javaMainArgs

    }

    void setJrrRunner2(int type) {
        if(SystemUtils.IS_OS_WINDOWS){
            if(type in [2,3]){

            }else{
                throw new IllegalStateException("Invalid type : ${type}, allowed : 2 or 3")
            }
        }else{
            type = 2
        }
        File jrrStarterLibsDir = GitReferences.groovyClasspathDir.resolveToFile()
        setJrrRunner(type, jrrStarterLibsDir)
    }

    void setJrrRunner(int type, File jrrStarterLibsDir) {
        File jrrFile = jrrStarterLibsDir.child('jremoterun.jar')
        assert jrrFile.exists()
        javaArgs.add "-javaagent:${jrrFile.absolutePath}".toString()
        javaClasspath.addF jrrStarterLibsDir.child('groovy_custom.jar')
        javaClasspath.addF jrrStarterLibsDir.child('groovy.jar')
        mainClass = groovyMainRunner
        javaMainArgs.add(0, GitReferences.groovyRunner.resolveToFile().absolutePath)
        javaMainArgs.add(1, type as String)
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
        process.consumeProcessOutput(System.out,System.err)
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
