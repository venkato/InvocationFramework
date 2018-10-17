package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JnaCoreCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    File baseDir
    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
        if (baseDir == null) {
            baseDir = GitReferences.jnaRepo.resolveToFile()
        }
        compilerPure.adder.addFileWhereClassLocated(JrrUtilities)

//        client.adder.addAll GroovyMavenIds.all
        compilerPure.javaVersion = '1.5'
        compilerPure.addInDir new File(baseDir, GitReferences.jnaCore.src)
        compilerPure.outputDir = new File(baseDir, 'build/classes2')
        compilerPure.outputDir.mkdirs()
    }

    File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),compilerPure.outputDir)
        dist = new File(baseDir, GitReferences.jnaCore.pathInRepo);
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
        ZipUtil.pack(compilerPure.outputDir, dist)
    }


    @Test
    void all() {
//        prepare()
        prepare()
        compilerPure.compile()
        zip()
    }


}
