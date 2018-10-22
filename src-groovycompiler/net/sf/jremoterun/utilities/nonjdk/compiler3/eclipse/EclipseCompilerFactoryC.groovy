package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
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

    GroovyCompiler groovyCompiler;

    GroovyCompilerParams params;

    EclipseCompilerFactoryC(GroovyCompiler groovyCompiler,  GroovyCompilerParams params) {
        this.groovyCompiler = groovyCompiler
        this.params = params;
    }

    @Override
    JavaCompiler createCompiler(CompilerConfiguration config) {
        javaCompilerC =  new EclipseJavaCompiler2C(config,groovyCompiler,params)
        return javaCompilerC;
    }
}
