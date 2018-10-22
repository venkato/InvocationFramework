package net.sf.jremoterun.utilities.nonjdk.compiler3.javac;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import net.sf.jremoterun.utilities.nonjdk.compiler3.javac.JavacJavaCompiler2C
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.javac.JavaCompiler
import org.codehaus.groovy.tools.javac.JavacCompilerFactory

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JavacCompilerFactoryC extends JavacCompilerFactory{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JavacJavaCompiler2C javaCompilerC ;

    GroovyCompiler groovyCompiler;
    GroovyCompilerParams params;

    JavacCompilerFactoryC(GroovyCompiler groovyCompiler,  GroovyCompilerParams params) {
        this.groovyCompiler = groovyCompiler
        this.params = params;
    }

    @Override
    JavaCompiler createCompiler(CompilerConfiguration config) {
        javaCompilerC =  new JavacJavaCompiler2C(config,groovyCompiler,params)
        return javaCompilerC;
    }
}
