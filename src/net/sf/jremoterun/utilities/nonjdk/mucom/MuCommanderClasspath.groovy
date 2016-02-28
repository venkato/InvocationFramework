package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.compile.GenericCompiler
import net.sf.jremoterun.utilities.nonjdk.git.GitRef

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.icu4j
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.log4jOld
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.logbackClassic
import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.logbackCore


@CompileStatic
class MuCommanderClasspath extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static GitRef gitSrcRef = new GitRef("https://github.com/mucommander/mucommander","src/main/java")

    public static List muCommanderDeps =
            LatestMavenIds.usefulMavenIdSafeToUseLatest+
            [
                    log4jOld,
                    icu4j,
                    logbackClassic,
                    logbackCore,

            new MavenId('org.asm-labs:junrar:0.8'),
            new MavenId('org.objectweb.joram:jftp:1.52'),
            new MavenId('com.jamesmurty.utils:java-xmlbuilder:1.1'),
            new MavenId('com.vmware.photon.controller:photon-vsphere-adapter-sdk:0.6.44'),
            new MavenId('net.java.dev.jets3t:jets3t:0.7.1'),
            new MavenId('com.beust:jcommander:1.72'),
            new MavenId('net.iharder:base64:2.3.9'),
            new MavenId('org.apache.hadoop:hadoop-core:1.2.1'),
            new MavenId('org.apache.mahout.hadoop:hadoop-core:0.20.1'),

    ]

    @Deprecated
    static void addLibs(File base, AddFilesToClassLoaderGroovy b) {
        b.addAllJarsInDir new File(base, "build/libs/")

        base.listFiles().toList().findAll { it.name.startsWith('mucommander-') }.findAll { it.directory }.each {
            b.addAllJarsInDir new File(it, "build/libs/")
        }
    }

    static void addRuntimeLibs(AddFilesToClassLoaderGroovy b, File base) {
        b.addAll muCommanderDeps
        b.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        addResources(b,base)
    }

    static void addResources(AddFilesToClassLoaderGroovy b, File base) {
        b.add new File(base, "src/main/resources")
        base.listFiles().toList().findAll { it.name.startsWith('mucommander-') }.findAll { it.directory }.each {
            b.add new File(it, "src/main/resources")
        }
        b.add new MavenCommonUtils().getToolsJarFile()
    }

    File baseDir;

    void setBaseDir3(){
        CustomObjectHandlerImpl customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl
        baseDir = customObjectHandler.cloneGitRepo3.cloneGitRepo3(MuCommanderClasspath.gitSrcRef)
    }

    @Override
    void prepare() {
        setBaseDir3()
        params.javaVersion = "1.8"
        params.addInDir new File(baseDir, "src/main/java")

        client.adder.addF new File(baseDir, "libs/java-extension.jar")
        client.adder.addAll muCommanderDeps
        baseDir.listFiles().toList().findAll { it.name.startsWith('mucommander-') }.findAll { it.directory }.each {
            params.addInDir new File(it, "src/main/java")
        }
        params.outputDir = new File(baseDir, "build/out2")
    }
}
