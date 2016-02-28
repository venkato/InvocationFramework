package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.javac.JavaCompiler
import org.codehaus.groovy.tools.javac.JavacCompilerFactory

import java.util.logging.Logger

// Without subfiles https://github.com/blackdrag/Groja
// another java compiler
// 1. org.codehaus.janino:janino:xxx
// 2. https://github.com/javaparser/javaparser

@CompileStatic
class EclipseCompilerFactoryC extends JavacCompilerFactory{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseJavaCompiler2C javaCompilerC ;

    GroovyCompiler groovyCompiler

    EclipseCompilerFactoryC(GroovyCompiler groovyCompiler) {
        this.groovyCompiler = groovyCompiler
    }

    @Override
    JavaCompiler createCompiler(CompilerConfiguration config) {
        javaCompilerC =  new EclipseJavaCompiler2C(config,groovyCompiler)
        return javaCompilerC;
    }
}
