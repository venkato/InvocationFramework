package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JnaJvmtiCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    File baseDir

    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.junit,
            CustObjMavenIds.commonsLoggingMavenId,
            LatestMavenIds.commonsCollection,
            LatestMavenIds.log4jOld,
            CustObjMavenIds.commonsIo,
            GitMavenIds.jgit,
    ]

    void prepare() {
        if (baseDir == null) {
            baseDir = GitSomeRefs.jnaRepo.resolveToFile()
        }
        client.adder.addAll mavenIds
        client.adder.add GitReferences.jnaCore
        client.adder.addFileWhereClassLocated(JrrUtilities)

//        client.adder.addAll GroovyMavenIds.all

        params.printWarning = false
        params.javaVersion = '1.5'
        params.addInDir new File(baseDir, GitReferences.jnaJvmti.src)
        params.outputDir = new File(baseDir, 'build/classesjvmti')
        params.outputDir.mkdirs()
    }

    File dist

    void zip() {
        if(dist==null) {
            dist = new File(baseDir, GitReferences.jnaJvmti.pathInRepo);
        }
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
