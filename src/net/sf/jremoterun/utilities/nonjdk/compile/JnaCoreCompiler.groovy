package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JnaCoreCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    File baseDir


    void prepare() {
        if (baseDir == null) {
            baseDir = GitReferences.jnaRepo.resolveToFile()
        }
        client.adder.addFileWhereClassLocated(JrrUtilities)

//        client.adder.addAll GroovyMavenIds.all

        params.printWarning = false
        params.javaVersion = '1.5'
        params.addInDir new File(baseDir, GitReferences.jnaCore.src)
        params.outputDir = new File(baseDir, 'build/classes2')
        params.outputDir.mkdirs()
    }

    File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),params.outputDir)
        dist = new File(baseDir, GitReferences.jnaCore.pathInRepo);
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
        ZipUtil.pack(params.outputDir, dist)
    }


    @Test
    void all() {
//        prepare()
        prepare()
        compile()
        zip()
    }


}
