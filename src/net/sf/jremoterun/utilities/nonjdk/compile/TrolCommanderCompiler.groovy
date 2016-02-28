package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class TrolCommanderCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir
//
//    void addCompilePrepand(AddFilesToUrlClassLoaderGroovy b) {
////        b.add new File(baseDir, 'lib/runtime/rsyntaxtextarea-3.0.0-SNAPSHOT.jar')
////        b.add GitReferences.pty4j
////        b.add new File(baseDir, 'assembly/lib/pty4j-0.3.jar')
//    }


    void prepare() {
        if (baseDir == null) {
            CustomObjectHandler customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
            assert customObjectHandler != null
            baseDir = customObjectHandler.resolveToFile(TrolCommanderCompilerAux.trolBase)
        }
        params.javaVersion = '1.8'
        params.addInDir new File(baseDir, TrolCommanderCompilerAux.trolBinAndSrc.src)
        params.outputDir = new File(baseDir, 'build/classes')
        params.outputDir.mkdirs()
        AddFilesToUrlClassLoaderGroovy b = client.adder
        b.addAll Log4j2MavenIds.all
//        addCompilePrepand(b)
//        b.add new MavenId('ch.qos.logback:logback-classic:0.9.20')
        b.addAll TrolCommanderCompilerAux.mavenIds
        TrolCommanderCompilerAux.addCompileAndRuntime(baseDir, b)
    }


    @Test
    @Override
    void all2() {
        super.all2()
        zipp()
    }

    File zipp() {
        File classes2 = TrolCommanderCompilerAux.trolBinAndSrc2.resolveToFile()
        classes2.mkdir()
        FileUtils.copyDirectory(params.outputDir, classes2)
        File zipFile = new File(baseDir, TrolCommanderCompilerAux.trolBinAndSrc.pathInRepo)
        zipFile.delete()
        assert !zipFile.exists()
        ZipUtil.pack(params.outputDir, zipFile)
        return zipFile;
    }


}
