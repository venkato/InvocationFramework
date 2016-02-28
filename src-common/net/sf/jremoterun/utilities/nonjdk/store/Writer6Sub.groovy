package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class Writer6Sub extends Writer5Class {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String className = 'Config'
    public static String varName ='b'

    Writer6Sub() {
        addImport(GroovyRunnerConfigurator2)
        addImport(CompileStatic)
        addImport(ClasspathConfigurator)
        addImport(GroovyConfigLoader)
        addImport(AddFilesToClassLoaderGroovy)
        addImport(getConfigClass())
    }


    abstract Class getConfigClass();

    @Override
    String getClassDeclarationName() {
        return "class ${className} extends ${GroovyConfigLoader.simpleName}<${getConfigClass().simpleName}> {".toString()
    }

    @Override
    String getMainMethod() {
        MethodClosure method = GroovyConfigLoader.loadConfigMethod
        return "void ${method.getMethod()}(${getConfigClass().simpleName} ${varName}){".toString()
    }

}
