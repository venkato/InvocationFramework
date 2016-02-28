package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
class Writer3Sub extends Writer3{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String className = 'Config'

    static MethodClosure doConfigMethod =(MethodClosure) GroovyRunnerConfigurator2.&doConfig;



    Writer3Sub() {
        addImport(GroovyRunnerConfigurator2)
        addImport(CompileStatic)
    }

    String buildResult() {
        List<String> res = header + importss.collect { "import ${it} ;" as String};

//        res += ['']
//        res += ['@CompileStatic']
        res += ["class ${className} extends ${GroovyRunnerConfigurator2.simpleName} {" as String]
//        res += ["${className}(Binding bi){super(bi)}" as String]
//        res += ['@Override']
        res += ["void ${doConfigMethod.method}(){" as String]
        res += ['']
        res += body
        res += ['']
        res += ['} } ']
        String res4= res.join('\n')
        GroovyFileChecker.analize(res4)
        return res4;
    }

    @java.lang.Override
    String generateGetProperty(String propName) {
        return " getVar('${propName}') "
    }
}
