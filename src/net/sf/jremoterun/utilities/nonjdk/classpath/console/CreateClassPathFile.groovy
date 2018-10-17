package net.sf.jremoterun.utilities.nonjdk.classpath.console;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovySave
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGitRefSup
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.compile.JeditTermCompilerConsoleCompiler;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CreateClassPathFile {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void createGroovyDefaultClasspath(File groovyClasspathFileResult){
        ClassPathCalculatorGitRefSup calculator = new ClassPathCalculatorGitRefSup()
        AddFilesToClassLoaderGroovySave adder = calculator.addFilesToClassLoaderGroovySave
        adder.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        CutomJarAdd.addCustom(adder)
        groovyClasspathFileResult.text = calculator.calcAndSave()
    }

}
