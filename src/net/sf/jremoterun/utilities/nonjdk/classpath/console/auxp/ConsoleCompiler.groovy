package net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.compile.GenericCompiler
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompiler
import net.sf.jremoterun.utilities.nonjdk.methodrunner.AuxMethodRunner

import java.util.logging.Logger

@CompileStatic
class ConsoleCompiler implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.gmrp
        List<String> args = gmrp.args
        if(args.size()<2){
            throw new IllegalArgumentException("too low arguments, needed : <indir1,indir2> <outdir>")
        }
        List<File> inDirs = args[0].tokenize(',').collect {it as File}
        inDirs.each {assert it.exists()}
        inDirs = inDirs.collect {it.canonicalFile.absoluteFile}
        assert inDirs.size()>0
        File outDir = args[1] as File
        outDir.mkdirs()
        assert outDir.exists()
        outDir = outDir.canonicalFile.absoluteFile
        log.info "compiling : ${inDirs}"
        GenericCompiler genericCompiler = new GenericCompiler(){
            @Override
            void prepare() {

            }
        }
        inDirs.each {
            genericCompiler.addInDir(it)
        }
        genericCompiler.params.outputDir = outDir
        AddFilesToUrlClassLoaderGroovy adder = genericCompiler.client.adder
        adder.add LatestMavenIds.eclipseJavaCompiler
        adder.add LatestMavenIds.eclipseJavaAstParser
        adder.addFilesFromGmrp()
        genericCompiler.compile()
        inDirs.each {
            CopyResourcesFromDirToDir.copyResourcesFromDirToDir(it,outDir)
        }
        gmrp.addFilesToClassLoader.addFile outDir
        args.remove(0)
        args.remove(0)
        log.info "compiled : ${outDir}"
    }


}
