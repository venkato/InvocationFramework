package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class Writer5Class extends Writer3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    String className = 'Config'

    static MethodClosure addCpMethod = (MethodClosure) ClasspathConfigurator.&addCp;


    Writer5Class() {
        addImport(GroovyRunnerConfigurator2)
        addImport(CompileStatic)
        addImport(ClasspathConfigurator)
    }


    abstract String getClassDeclarationName()
//        return "class ${className} extends ${ClasspathConfigurator.simpleName} {"


    abstract String getMainMethod();

    @Override
    String buildResult() {
        List<String> res = header + importss.collect { "import ${it} ;" as String };
        res.add getClassDeclarationName()
//        res += ["${className}(Binding bi){super(bi)}" as String]
//        res += ['@Override']
//        res += ["void ${addCpMethod.method}(AddFilesToClassLoaderGroovy b){" as String]
        res.add getMainMethod()
        res += ['']
        res += body
        res += ['']
        res += ['} } ']
        String res4 = res.join('\n')
        GroovyFileChecker.analize(res4)
        return res4;
    }

    @java.lang.Override
    String generateGetProperty(String propName) {
        return " getVar('${propName}') "
    }
}
