package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import com.michaelalynmiller.jnaplatext.win32.ProcessUtils
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2
import net.sf.jremoterun.utilities.groovystarter.st.PrintSelfHelp
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import net.sf.jremoterun.utilities.nonjdk.winutils.WinCmdUtils2
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
class GradleWrapperRunner implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClRef gradleWrapperCl = new ClRef('org.gradle.wrapper.GradleWrapperMain')

    @Override
    void run() {
        doJob()
    }

    void doJob(){
        AddFilesToUrlClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrp.addFilesToClassLoader
        File wrapperJar = new File('gradle/wrapper/gradle-wrapper.jar');
        //wrapperJar = wrapperJar.canonicalFile.absoluteFile
        //assert wrapperJar.exists()
        adder.add(wrapperJar)
        System.setProperty('org.gradle.appname','gradlew');
        GroovyMethodRunnerParams2.gmrp2.mainClass = gradleWrapperCl
//        GroovyMethodRunnerParams2.gmrp2.runMainJavaMethod = true

    }

}
