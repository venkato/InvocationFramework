package net.sf.jremoterun.utilities.nonjdk.compiler3

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CompileRequestRemote implements CompilerRequest {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    ClassLoader classLoader12 = getClass().getClassLoader();

    @Override
    void compile(GroovyCompilerParams params) {
        params.classNameRunnerWithParams.each {
            RunnableWithParamsFactory.fromClass3(it, classLoader12, params)
        }

        URL groovyJar = JrrUtils.getClassLocation(GroovyObject)
        log.fine "groovy jar = ${groovyJar}"
        GroovyCompiler groovyCompiler = new GroovyCompiler(params)
        if (params.javaVersion != null) {
            groovyCompiler.setJavaVersion(params.javaVersion);
        }
        params.dirs.unique().each {
            groovyCompiler.addClassesInDirForCompile(it)
        }
        File[] files2 = (File[])params.files.unique().toArray(new File[0])
        groovyCompiler.unit.addSources(files2)
//        GroovyClassLoader cl = JrrClassUtils.currentClassLoaderGroovy
        groovyCompiler.additionalFlags.addAll(params.additionalFlags)
        groovyCompiler.compile()
    }
}
