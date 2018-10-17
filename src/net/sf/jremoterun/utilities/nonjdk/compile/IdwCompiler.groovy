package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class IdwCompiler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    File baseDir

    IdwCompiler(File baseDir) {
        this.baseDir = baseDir
    }

    void prepare(){
        compilerPure.javaVersion = '1.5'
        compilerPure.addInDir baseDir.child('src')
        File f  = baseDir.child('build')
        f.mkdir()
        compilerPure.outputDir = f.child('classes')
    }

}
