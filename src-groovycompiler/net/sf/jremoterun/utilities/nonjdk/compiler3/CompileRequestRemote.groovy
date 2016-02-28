package net.sf.jremoterun.utilities.nonjdk.compiler3

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CompileRequestRemote implements CompilerRequest {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void compile(GroovyCompilerParams params) {
        URL groovyJar = JrrUtils.getClassLocation(GroovyObject)
        log.fine "groovy jar = ${groovyJar}"
        GroovyCompiler groovyCompiler = new GroovyCompiler(params.outputDir,params.eclipseCompiler)
        if(params.javaVersion!=null){
            groovyCompiler.setJavaVersion(params.javaVersion);
        }
        params.dirs.unique().each {
            groovyCompiler.addClassesInDirForCompile(it)
        }
        groovyCompiler.unit.addSources(params.files.unique().toArray(new File[0]))
//        GroovyClassLoader cl = JrrClassUtils.currentClassLoaderGroovy
        groovyCompiler.additionalFlags.addAll(params.additionalFlags)
        groovyCompiler.compile()
    }
}
