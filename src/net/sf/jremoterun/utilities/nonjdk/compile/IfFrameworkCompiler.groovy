package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.AsmOw
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.BouncyCastleMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitlabLibsMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MaryDependentMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIdRandom
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenResolverMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.NexusSearchMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Okhttp3MavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SshdMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class IfFrameworkCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [
            LatestMavenIds.logbackClassic,
            LatestMavenIds.logbackCore,
            LatestMavenIds.guavaMavenId,
            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.rstaui,
            LatestMavenIds.rstaAutoComplete,
            Log4j2MavenIds.slf4j_impl,
            SshdMavenIds.core,
            SshdMavenIds.netty,
            SshdMavenIds.scp,
            SshdMavenIds.common,
            GitMavenIds.jgit_ssh_jsch,
            LatestMavenIds.jline2,
            LatestMavenIds.jline3,
            CustObjMavenIds.commonsIo,
            LatestMavenIds.quickfixj,
            LatestMavenIds.minaCore,
            BouncyCastleMavenIds.bctls_jdk15on,
            BouncyCastleMavenIds.bcpkix_jdk15on,
            GitlabLibsMavenIds.coreLib,
            GitlabLibsMavenIds.jakartaWsRsApi,
            // janino used in idea plugin only
            LatestMavenIds.javaCompiler2Janino,
            LatestMavenIds.javaCompilerJaninoCommon,
            LatestMavenIds.plexusClassworlds,
            LatestMavenIds.jodaTime,
            LatestMavenIds.svnNativeClintWrapper,
            LatestMavenIds.svnClientAdapterJavahlUseless,
            LatestMavenIds.svnClientAdapterMainUseless,
            LatestMavenIds.maven_resolver_provider,
            LatestMavenIds.plexus_utils,
            LatestMavenIds.commonsCli,
            Okhttp3MavenIds.okhttp,
            MavenMavenIdRandom.sharedUtils,
            MavenMavenIdRandom.wagonProviderApi,
//            MavenMavenIds.embedder,
//            MavenMavenIds.compat,
//            MavenMavenIds.model,
//            LatestMavenIds.jsoup,
    ]

    File baseDir


    void addIdw() {
        client.adder.add CutomJarAdd1.downloadIdw()
    }

    static void addLibs(AddFilesToClassLoaderGroovy adder){
        adder.addFileWhereClassLocated JdkLogFormatter
        adder.addFileWhereClassLocated JrrClassUtils
        adder.addFileWhereClassLocated JavaVMClient
        adder.addAll DropshipClasspath.allLibsWithGroovy
        adder.addAll GroovyMavenIds.all
        adder.addAll MavenResolverMavenIds.all
        adder.addAll MavenMavenIds.all
        adder.addAll mavenIds
        adder.addAll GitlabLibsMavenIds.fastXmls
        adder.addAll GitlabLibsMavenIds.glassfish
        adder.addAll NexusSearchMavenIds.all
        adder.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        adder.addAll AsmOw.all
        adder.addAll MaryDependentMavenIds.values().toList()
        // adder.add GroovyMavenIds.groovyCore
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        CutomJarAdd.addCustom(adder)

    }

    void prepare() {
        if (baseDir == null) {
            baseDir = client.ifDir
        }
        // if provider loaded java.nio.file.spi.FileSystemProvider, then
        // org.apache.sshd.client.subsystem.sftp.SftpFileSystemProvider loaded, which cause load
        // org.apache.sshd.common.io.IoServiceFactoryFactory
        // Could be 2 Factories, which it doesn't like and throw Exception 2 Factory found
        net.sf.jremoterun.utilities.nonjdk.shell.GroovySehllSshServiceSettings.setSshProps()
        params.printWarning = false
        params.javaVersion = '1.6'
        params.outputDir = baseDir.child('build/ifbuild')
        params.outputDir.mkdirs()
        params.addInDir GitReferences.jnaplatext.resolveToFile()
        addLibs(client.adder);
//        client.adder.addFileWhereClassLocated JdkLogFormatter
//        client.adder.addFileWhereClassLocated JrrClassUtils
//        client.adder.addFileWhereClassLocated JavaVMClient
//        client.adder.addAll DropshipClasspath.allLibsWithGroovy
//        client.adder.addAll GroovyMavenIds.all
//        client.adder.addAll mavenIds
//        client.adder.addAll NexusSearchMavenIds.all
//        client.adder.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
//        client.adder.addAll AsmOw.all
//        client.adder.addAll MaryDependentMavenIds.values().toList()
//        client.adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
//        CutomJarAdd.addCustom(client.adder)

        List<String> dirs = InfocationFrameworkStructure.dirs2
        dirs.each {
            params.addInDir(baseDir.child(it))
        }
        params.addInDir baseDir.child("groovycustom/src")
        params.addInDir baseDir.child(IfFrameworkSrcDirs.src_logger_ext_methods.dirName)

        addIdw()

        params.addTestClassLoaded JrrClassUtils
        params.addTestClassLoaded JdkLogFormatter
        params.addTestClassLoadedSameClassLoader(org.fusesource.jansi.AnsiRenderWriter)
        params.addTestClassLoadedSameClassLoader(org.codehaus.groovy.tools.shell.IO)
        params.addTestClassLoadedSameClassLoader(groovy.json.JsonSlurper)
        params.addTestClassLoadedSameClassLoader(groovy.util.AntBuilder)
        params.addTestClassLoadedSameClassLoader(groovy.util.slurpersupport.GPathResult)
//        log.info "finished"
        addToolsJar()
    }

    void addToolsJar() {
        File toolsJar = mcu.getToolsJarFile()
        if(toolsJar.exists()) {
            client.adder.add toolsJar
        }
    }



    void zipp(File destJar) {
        ZipUtil.pack(params.outputDir, destJar)
    }

    static ClRef ideaRedefinitionTester = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloaderTester')

    static ClRef commonRedefinitionTester = new ClRef('net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefinitionTester')

    void testIdeaClassReloader() {
        client.adder.addF params.outputDir
        testIdeaClassReloader2(client.loader)
    }

    static void testIdeaClassReloader2(ClassLoader cl) {
        // client.adder.addF params.outputDir
        ContextClassLoaderWrapper.wrap2(cl, {
            GroovyRunnerConfigurator2.createRunnerFromClass(commonRedefinitionTester, cl).run()
            GroovyRunnerConfigurator2.createRunnerFromClass(ideaRedefinitionTester, cl).run()

        })
    }




}
