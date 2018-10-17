package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.*
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JeditTermCompilerConsoleCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            LatestMavenIds.jcraft,
            LatestMavenIds.jcraftZlib,
            LatestMavenIds.guavaMavenIdNew,
            LatestMavenIds.log4jOld,
            LatestMavenIds.jnaPlatform,
            LatestMavenIds.jna,
            LatestMavenIds.jetbrainsAnnotations,
    ]

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    JeditTermCompilerConsoleCompiler() {
    }

//    File baseDir
    File buildDir


    void prepare() {
        detectBuildDir()
        buildDir.mkdir()
        assert buildDir.exists()
        compilerPure.outputDir = buildDir.child('jediterm_classes')
        compilerPure.outputDir.mkdirs()

        compilerPure.adder.addAll mavenIds

        compilerPure.addInDir GitReferences.pty4jSrc
        compilerPure.addInDir GitReferences.purejavacommTraffSrc
        JeditermBinRefs2.all.each { compilerPure.addInDir(it) }
        compilerPure.javaVersion = '1.8'
        log.info("out dir : ${compilerPure.outputDir}")
    }

    void detectBuildDir(){
        if (buildDir == null) {
            File baseDir = JeditermBinRefs2.terminal.ref.specOnly.resolveToFile()
            buildDir = baseDir.child('build')
        }
    }

    static File compileIfNeededS(){
        return new JeditTermCompilerConsoleCompiler().compileIfNeeded()
    }

    File compileIfNeeded(){
        detectBuildDir()
        File zipFile = buildDir.child('jediterm.jar')
        if(!zipFile.exists()){
            prepare()
            compilerPure.compile()
            zipp()
        }
        assert zipFile.exists()
        return zipFile
    }


    File zipp() {
        assert buildDir.exists()
        File zipFile = buildDir.child('jediterm.jar')
        zipFile.delete()
        assert !zipFile.exists()
        ZipUtil.pack(compilerPure.outputDir, zipFile)
        return zipFile;
    }


}
