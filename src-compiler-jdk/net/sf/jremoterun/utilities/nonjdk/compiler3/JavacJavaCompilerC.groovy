package net.sf.jremoterun.utilities.nonjdk.compiler3;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.javac.JavacJavaCompiler;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JavacJavaCompilerC extends JavacJavaCompiler{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CompilerConfiguration config2



    JavacJavaCompilerC(CompilerConfiguration config) {
        super(config)
        this.config2 = config
    }

    @Override
    void compile(List<String> files, CompilationUnit cu) {
        Map options = config2.getJointCompilationOptions();
        //def object = options.get('flags')
//        log.info "flags : ${object}"
        super.compile(files, cu)
    }
}
