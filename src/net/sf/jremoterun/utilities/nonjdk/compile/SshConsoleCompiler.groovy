package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.NexusSearchMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class SshConsoleCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            LatestMavenIds.logbackClassic,
            LatestMavenIds.logbackCore,
            LatestMavenIds.guavaMavenId,
            LatestMavenIds.junit,
            LatestMavenIds.sshd,
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.rstaui,
            LatestMavenIds.rstaAutoComplete,
//            LatestMavenIds.rstaLangSupport,
            LatestMavenIds.commonsCollection,
            Log4j2MavenIds.slf4j_impl,
            LatestMavenIds.jline2,
            LatestMavenIds.jline3,
            LatestMavenIds.commonsIo,
    ]

    SshConsoleCompiler() {
    }

    void addIdw() {
        client.adder.add CutomJarAdd1.downloadIdw()
    }

    File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = client.ifDir
        }
        params.printWarning = false
        params.outputDir = baseDir.child('build/sshbuild')
        params.outputDir.mkdirs()

        params.addInDir GitReferences.jnaplatext.resolveToFile()
//        client.adderParent.addM LatestMavenIds.junit
        client.adder.addFileWhereClassLocated(JavaVMClient)
        client.adder.addFileWhereClassLocated(JdkLogFormatter)
        client.adder.add mcu.getToolsJarFile()
        client.adder.addAll mavenIds
        client.adder.addAll GroovyMavenIds.all
        client.adder.addAll DropshipClasspath.allLibsWithGroovy
        client.adder.addAll NexusSearchMavenIds.all
        client.adder.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        CutomJarAdd.addCustom(client.adder)
        List<String> dirs = InfocationFrameworkStructure.dirs2
        dirs.each {
            params.addInDir baseDir.child(it)
        }
        params.addTestClassLoaded org.fusesource.jansi.AnsiRenderWriter

        addIdw()
        params.javaVersion = '1.6'

//        log.info "finished"
    }


    File zipp() {
        File zipFile = baseDir.child('build/sshConsole.jar')
        zipFile.delete()
        assert !zipFile.exists()
        ZipUtil.pack(params.outputDir, zipFile)
        return zipFile;
    }


}
