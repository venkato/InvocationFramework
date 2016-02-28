package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class RstaCoreCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static CustomObjectHandlerImpl handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl

    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.log4jOld,
            LatestMavenIds.rstaui,
            CustObjMavenIds.git,
            LatestMavenIds.jsoup,
            LatestMavenIds.junit,
            CustObjMavenIds.commnonsLang,
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.jodaTime,
            LatestMavenIds.commonsCodec,
            LatestMavenIds.commonsIo,
            DropshipClasspath.ivyMavenId,
    ]

//    void prepare() {
//
//    }

    void prepare() {
        client.adder.addGenericEnteries(mavenIds)
        client.adder.addFileWhereClassLocated(JrrUtilities)

        client.adder.addAll GroovyMavenIds.all
        client.adder.add GitReferences.rsta
        client.adder.add GitReferences.rstaAutoCompetion
//        client.adder.addFileWhereClassLocated(AddFileWithSources)
        client.adder.addFileWhereClassLocated(SetConsoleOut2)

        params.printWarning = false
        params.javaVersion = '1.6'
        params.addInDir new File(client.ifDir,'src-rsta-core')
        params.addInDir new File(client.ifDir,'src-common')
        params.outputDir = new File(client.ifDir,'build/rsta-core')
        params.outputDir.mkdirs()
    }

    File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),params.outputDir)
        dist= new File(client.ifDir,'dist/rsta-core.jar');
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
        ZipUtil.pack(params.outputDir,dist)
    }


    @Test
    void all() {
//        prepare()
        prepare()
        compile()
        zip()
    }


}
