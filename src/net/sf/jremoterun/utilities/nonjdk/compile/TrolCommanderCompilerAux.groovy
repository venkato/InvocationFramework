package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec

import java.util.logging.Logger

@CompileStatic
class TrolCommanderCompilerAux {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GitSpec trolBase = new GitSpec("https://github.com/venkato/mucommander", null, 'develop', null)

    public
    static GitBinaryAndSourceRef trolBinAndSrc = new GitBinaryAndSourceRef(trolBase, 'build/trolCommander.jar', 'src/main')

    public
    static GitBinaryAndSourceRef trolBinAndSrc2 = new GitBinaryAndSourceRef(trolBase, 'build/classes2/', 'src/main')

    static {
        trolBinAndSrc.branch = trolBase.branch
    }


    public static List mavenIds1 = [
            CustObjMavenIds.slf4jApi
            , LatestMavenIds.log4jOld
            , LatestMavenIds.rstaui
            , LatestMavenIds.rsyntaxtextarea
            , LatestMavenIds.rstaLangSupport
            , LatestMavenIds.logbackCore
            , LatestMavenIds.logbackClassic
            , LatestMavenIds.jmdns
            , LatestMavenIds.jideOss
            , LatestMavenIds.jcifs
            , LatestMavenIds.icePdfCore
            , LatestMavenIds.icePdfViewer
            , LatestMavenIds.j2sshMaverick
            , LatestMavenIds.jna
            , LatestMavenIds.jnaPlatform
            , LatestMavenIds.fifeRtext
            , CustObjMavenIds.junrar1
            , LatestMavenIds.commonsNet
            , LatestMavenIds.commonsCli
            , LatestMavenIds.commonsCodec
            , LatestMavenIds.commonsCollection
            , LatestMavenIds.commonsCollection4
            , LatestMavenIds.commonsConfig
            , LatestMavenIds.commonsIo
            , CustObjMavenIds.commnonsLang
            , CustObjMavenIds.commonsLoggingMavenId
            , LatestMavenIds.httpClient
            , LatestMavenIds.xercesImpl
            , LatestMavenIds.xmlApisExt
            , LatestMavenIds.guavaMavenId
            , LatestMavenIds.icu4j
            , LatestMavenIds.jetbrainsAnnotations
            , LatestMavenIds.webDavClient

//            , GitReferences.jtermSsh
//            , GitReferences.jtermPty
//            , GitReferences.pty4j
//            , GitReferences.pureJavaCom
    ]


    public static List mavenIds2 = [
            new MavenId('org.codehaus.woodstox:stax2-api:4.1')
            , new MavenId('com.fasterxml.woodstox:woodstox-core:5.1.0')
            , new MavenId('org.apache.xmlgraphics:batik-all:1.9.1')
            , new MavenId('org.objectweb.joram:jftp:1.52')
            , new MavenId('net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:9.20-2.00beta')
            , new MavenId('net.sf.sevenzipjbinding:sevenzipjbinding:9.20-2.00beta')
            , new MavenId('org.htrace:htrace-core:3.0.4')
            , new MavenId('com.google.protobuf:protobuf-java:3.5.1')
            , new MavenId('org.apache.sanselan:sanselan:0.97-incubator')
            , new MavenId('com.google.code.gson:gson:2.8.2')
            , new MavenId('com.github.stephenc.java-iso-tools:iso9660-writer:2.0.1')
            , new MavenId('com.github.stephenc.java-iso-tools:sabre:2.0.1')
            , new MavenId('com.jcraft:jzlib:1.1.3')
            , new MavenId('net.java.dev.jets3t:jets3t:0.7.2')
            , new MavenId('org.json:json:20180130')
            , new MavenId('com.mashape.unirest:unirest-java:1.4.9')
            , new MavenId('org.apache.hadoop:hadoop-auth:3.1.0')
            , new MavenId('org.apache.hadoop:hadoop-common:3.1.0')
            , new MavenId('org.apache.hadoop:hadoop-hdfs:3.1.0')
            , new MavenId('com.vmware.photon.controller:photon-vsphere-adapter-sdk:0.6.56')
    ]


    public static List mavenIds = mavenIds1 + mavenIds2

    static void addRuntimeResources(File baseDir, AddFilesToClassLoaderGroovy b) {
        b.add new File(baseDir, 'res/runtime')
        b.add new File(baseDir, 'res/package')
        b.add new File(baseDir, 'res/jar/services')
        b.add new File(baseDir, 'src/main')
    }


    static void addRuntimeAll(AddFilesToClassLoaderGroovy b) {
        CustomObjectHandler customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        assert customObjectHandler != null
        File baseDir = customObjectHandler.resolveToFile(trolBase)

        b.addAll Log4j2MavenIds.all
        b.addAll TrolCommanderCompilerAux.mavenIds
        b.add new JeditTermCompilerConsoleCompiler().compileIfNeeded()
        addCompileAndRuntime(baseDir, b)
        addRuntimeResources(baseDir, b)
        b.add trolBinAndSrc2
    }

    static void addCompileAndRuntime(File baseDir, AddFilesToClassLoaderGroovy b) {
        b.add new File(baseDir, 'assembly/lib/AppleJavaExtensions-1.6.jar')
        b.add new File(baseDir, 'assembly/lib/com.realityinteractive.imageio.tga.jar')
        b.add new File(baseDir, 'assembly/lib/javadjvu.jar')
        b.add new File(baseDir, 'assembly/lib/quaqua-native.jar')
        b.add new File(baseDir, 'assembly/lib/quaqua.jar')
        b.add new File(baseDir, 'assembly/lib/ui.jar')
        b.add new File(baseDir, 'assembly/lib/yanfs-1.2.jar')
        b.add new File(baseDir, 'lib/runtime/image4j.jar')
        b.add new File(baseDir, 'lib/runtime/djvuframe.jar')
    }


}
