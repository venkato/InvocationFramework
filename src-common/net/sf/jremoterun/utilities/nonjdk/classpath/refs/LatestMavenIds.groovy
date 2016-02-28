package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath

@CompileStatic
enum LatestMavenIds implements MavenIdContains {

    // https://github.com/Sciss/DockingFrames
    log4jOld('log4j:log4j:1.2.17')
    , gnuGetOpt('gnu.getopt:java-getopt:1.0.13')
    ,
    @Deprecated
    git(CustObjMavenIds.git),
    @Deprecated
    zeroTurnaroundZipUtil(CustObjMavenIds.zeroTurnaroundZipUtil),
    @Deprecated
    slf4jApi(CustObjMavenIds.slf4jApi),
    @Deprecated
    commonsLoggingMavenId(CustObjMavenIds.commonsLoggingMavenId),
    @Deprecated
    xmlApiId(DropshipClasspath.xmlApis),
    @Deprecated
    commnonsLang(CustObjMavenIds.commnonsLang),
    @Deprecated
    jdom('org.jdom:jdom:1.1'),
    @Deprecated
    junrar1(CustObjMavenIds.junrar1),
    @Deprecated
    compressAbstraction(CustObjMavenIds.compressAbstraction)

    , swtWin('org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:3.106.3')
    , eclipseWorkbench('org.eclipse.platform:org.eclipse.ui.workbench:3.110.1')
    , awsS3('com.amazonaws:aws-java-sdk-s3:1.11.275')
    , jnrJffi('com.github.jnr:jffi:1.2.17')
    , jniCodeGenerator('org.fusesource.hawtjni:hawtjni-example:1.16')
    , sshd('org.apache.sshd:sshd-core:1.7.0')
    ,
    // use eclipseJavaAstParser
    eclipseJavaCompiler('org.eclipse.jdt:ecj:3.14.0')
//    eclipseJavaCompiler('org.eclipse.jdt.core.compiler:ecj:4.6.1')
    , eclipseJavaAstParser('org.eclipse.jdt:org.eclipse.jdt.core:3.13.102')
    , logbackClassic('ch.qos.logback:logback-classic:1.2.3')
    , logbackCore('ch.qos.logback:logback-core:1.2.3')
    , icu4j('com.ibm.icu:icu4j:61.1')
    , jodaTime('joda-time:joda-time:2.9.9')
    , junit('junit:junit:4.12')
    , rstaui("com.fifesoft:rstaui:2.6.1")
    , rstaAutoComplete("com.fifesoft:autocomplete:2.6.1")
    , fifeRtext("com.fifesoft.rtext:fife.common:2.6.3")
    , rstaLangSupport("com.fifesoft:languagesupport:2.6.0")
    , rsyntaxtextarea("com.fifesoft:rsyntaxtextarea:2.6.1")
    , jna("net.java.dev.jna:jna:4.5.1")
    , jnaPlatform("net.java.dev.jna:jna-platform:4.5.1")
    , jline2('jline:jline:2.12')
    , jline3('org.jline:jline:3.6.0')
    , mailJavax('javax.mail:mail:1.4.7')
    , mailSun('com.sun.mail:javax.mail:1.6.0')
    , mailCommons('org.apache.commons:commons-email:1.5')
    , mailVertx('io.vertx:vertx-mail-client:3.5.2')
    , httpWinSupport('org.apache.httpcomponents:httpclient-win:4.5.5')
    , httpCore('org.apache.httpcomponents:httpcore:4.4.9')
    , httpClient("org.apache.httpcomponents:httpclient:4.5.5")
    , commonsHttpOld('commons-httpclient:commons-httpclient:3.1')
    , portForward('net.kanstren.tcptunnel:tcptunnel:1.1.2')
    // jediterm failed if higher version of guava
    , guavaMavenId('com.google.guava:guava:21.0')
    , xmlApisExt('xml-apis:xml-apis-ext:1.3.04')
    , xercesImpl('xerces:xercesImpl:2.11.0')
    , jfreeCommon('org.jfree:jcommon:1.0.24')
    , yandexDisk('com.yandex.android:disk-restapi-sdk:1.03')
    , jfreeChart('org.jfree:jfreechart:1.5.0')
    , rhino('org.mozilla:rhino:1.7.10')
    , commnonsLang3('org.apache.commons:commons-lang3:3.7')
    , commonsCollection('commons-collections:commons-collections:3.2.2')
    , commonsCodec('commons-codec:commons-codec:1.11')
    , servletApi('org.apache.tomcat:servlet-api:6.0.53')
    , jmeld('org.devocative:jmeld:3.2')
    , javaRepl('com.javarepl:javarepl:428')
    , groovySshShell('me.bazhenov.groovy-shell:groovy-shell-server:1.7.2')
    , dropbox('com.dropbox.core:dropbox-core-sdk:3.0.8')
    , javaCompiler1('com.github.javaparser:javaparser-core:3.6.5')
    , javaCompiler2Janino('org.codehaus.janino:janino:3.0.8')
    , webDavClient('com.github.lookfirst:sardine:5.8')
    , eclipseGroovyBatchCompiler('org.codehaus.groovy:groovy-eclipse-batch:2.4.13-01')
    , icePdfCore('org.icepdf.os:icepdf-core:6.2.2')
    , icePdfViewer('org.icepdf.os:icepdf-viewer:6.2.2')
    // comparision : http://ssh-comparison.quendi.de/comparison/cipher.html
    , j2sshMaverick('com.sshtools:j2ssh-maverick:1.5.5')
    , commonsNet('commons-net:commons-net:3.6')
    , commonsCollection4('org.apache.commons:commons-collections4:4.1')
    , jansi('org.fusesource.jansi:jansi:1.16')
    , jsoup('org.jsoup:jsoup:1.11.3')
    , jcraft('com.jcraft:jsch:0.1.54')
    , jideOss('com.jidesoft:jide-oss:3.6.18')
    , jmdns('javax.jmdns:jmdns:3.4.1')
    , jcifs('jcifs:jcifs:1.3.17')
    , commonsCli('commons-cli:commons-cli:1.4')
    , commonsConfig('commons-configuration:commons-configuration:1.10')
    , asmOw2All('org.ow2.asm:asm-all:5.2')
    , jetbrainsAnnotations('org.jetbrains:annotations-java5:16.0.2')
    , mx4j('mx4j:mx4j-tools:3.0.1')
    , trileadSsh('com.trilead:trilead-ssh2:1.0.0-build221')
    , cssParser('org.w3c.css:sac:1.3')
    , cssParser2('net.sf.cssbox:jstyleparser:3.2')
    , cssParser3('net.sourceforge.cssparser:cssparser:0.9.24')
    , commonsCompress('org.apache.commons:commons-compress:1.16.1')
    , pureJavaComm('com.github.purejavacomm:purejavacomm:1.0.2.RELEASE')
    , networkTestFramework('com.github.netcrusherorg:netcrusher-core:0.10')
    , kryoSerializer('com.esotericsoftware:kryo-shaded:4.0.2')
    , objenesis('org.objenesis:objenesis:2.6')
    , commonsIo(CustObjMavenIds.commonsIo)
    // http://repo1.maven.org/maven2/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.pom
    , commonsIoBad('org.apache.commons:commons-io:1.3.2')
    , jasperreports('net.sf.jasperreports:jasperreports:6.5.1')
    , quartz('org.quartz-scheduler:quartz:2.3.0')
    , winKillProcessTree('org.jvnet.winp:winp:1.25')
    , hamcrest('org.hamcrest:java-hamcrest:2.0.0.0')
    , svnKit(CustObjMavenIds.svnKit)
    , fernflowerJavaDecompiler('org.jboss.windup.decompiler.fernflower:windup-fernflower:1.0.0.20171018')
    , fernflowerLogger('org.jboss.windup.decompiler:decompiler-fernflower:4.0.1.Final')
    , fontChooser('io.github.dheid:fontchooser:2.3')
    , opencsv("com.opencsv:opencsv:4.2")
//    , jasypt('org.jasypt:jasypt:1.9.2')
    , eclipseGitHubApi('org.eclipse.mylyn.github:org.eclipse.egit.github.core:5.0.0.201806131550-r')
    ;


    MavenId m;


    LatestMavenIds(String m2) {
        this.m = new MavenId(m2)
    }

    LatestMavenIds(MavenIdContains m) {
        this.m = m.getM()
    }

    static MavenCommonUtils mcu = new MavenCommonUtils()

    public static List<? extends MavenIdContains> jol = [
            new MavenId('org.openjdk.jol:jol-samples:0.9'),
            new MavenId('org.openjdk.jol:jol-cli:0.9'),
            new MavenId('org.openjdk.jol:jol-core:0.9'),
    ]

//            'com.google.http-client:google-http-client:1.21.0'),
//            'com.google.oauth-client:google-oauth-client:1.21.0'),

    public
    static List<? extends MavenIdContains> loggingPrefix = [CustObjMavenIds.commonsLoggingMavenId, log4jOld]


    public
    static List<? extends MavenIdContains> specific = [CustObjMavenIds.git, junit, CustObjMavenIds.slf4jApi, jnrJffi, portForward, jna, jnaPlatform, gnuGetOpt, jodaTime, rstaui, eclipseJavaCompiler, eclipseJavaAstParser,rsyntaxtextarea, jfreeCommon, jfreeChart, CustObjMavenIds.commnonsLang, commonsCollection, commnonsLang3, commonsCodec, groovySshShell, javaCompiler1, icePdfCore, icePdfViewer, j2sshMaverick, commonsNet, commonsCollection4, jansi, jsoup, jcraft, httpClient, httpCore, jideOss, jmdns, jcifs, mx4j, trileadSsh, cssParser, commonsHttpOld, pureJavaComm, cssParser3, asmOw2All, compressAbstraction, commonsCompress, jasperreports, quartz, winKillProcessTree, svnKit, fernflowerJavaDecompiler, fernflowerLogger, fontChooser, opencsv,]


    public
    static List<? extends MavenIdContains> usefulMavenIdSafeToUseLatest = DropshipClasspath.allLibsWithoutGroovy + loggingPrefix + (List) AntMavenIds.all + (List) Log4j2MavenIds.all + (List) specific + (List) CustObjMavenIds.all


    static MavenIdContains filterOnMavenId(MavenId mavenId) {
        String group = mavenId.groupId;
        String artifact = mavenId.artifactId
        List<? extends MavenIdContains> lastest = usefulMavenIdSafeToUseLatest
        List<String> lastestGrousp = lastest.collect { it.m.groupId }.unique()
        switch (mavenId) {
        // TODO delete goory from here
            case { group == 'org.codehaus.groovy' }:
            case { artifact == 'guava-jdk5' }:
            case { group == 'ch.qos.logback' }:
            case { group == 'asm' }:
            case { artifact == 'languagesupport' }:
            case { artifact == 'jcl-over-slf4j' }:
            case { artifact == 'google-collections' }:
                return null
            case { artifact == 'commons-logging-api' }:
                return commonsLoggingMavenId
            case { mavenId.isGroupAndArtifact(jline2) }:
                return jline2
            case { mavenId.isGroupAndArtifact(xmlApiId) }:
                return xmlApiId
            case { group == CustObjMavenIds.commonsLoggingMavenId.m.groupId }:
                return CustObjMavenIds.commonsLoggingMavenId
            case { mavenId.isGroupAndArtifact(commonsIoBad) }:
                commonsIo
            case { mavenId.isGroupAndArtifact(guavaMavenId) }:
                return guavaMavenId
            case { lastestGrousp.contains(group) }:
                return mcu.findLatestMavenOrGradleVersion2(mavenId)
            default:
                return mavenId;
        }
    }


}
