package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.ideasdk


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.UserBintrayRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorEnumConverter
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.AllMavenIdsRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.compile.IdeaPluginCompiler
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectStringComparator
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer4Sub;

import java.util.logging.Logger;

/**
 * Need keep only idea classes.
 * If need non idea classes better pull from public for: 1) have sources, 2) avoid version conflict
 */
@CompileStatic
class IdeaRuntimeClasspath {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //plugin id : org.jetbrains.kotlin

    public static List<String> acceptFiles = ['openapi.jar',
                                              'idea.jar',
                                              'forms_rt.jar',
                                              'external-system-rt.jar',
                                              'extensions.jar',
                                              'credential-store.jar',
                                              'configuration-store-impl.jar',
                                              'jshell-protocol.jar',
                                              'java-impl.jar', 'java-api.jar', 'serviceMessages.jar', 'spellchecker.jar',
                                              'images.jar', 'Groovy.jar', 'aether-dependency-resolver.jar',
                                              'jshell-frontend.jar', 'built-in-server.jar', 'bootstrap.jar', 'util.jar', 'groovy_rt.jar',
                                              'annotations.jar', 'terminal.jar', 'kotlin-plugin.jar',
                                              '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '',]

    public static List<String> startwithAccept = ['platform-', 'jps-', 'intellij-', 'idea_', 'randomForestRegressor-', 'sa-jdwp-',
                                                  'rd-', 'jetCheck-', 'java-compatibility-', 'exception-analyzer-api-',
                                                  'groovy-', 'tips-intellij-idea-community-', 'serviceMessages-', '-', '-', '-', '-', '-', '-', '-',]


    public static List<MavenIdAndRepo> manualMavenIds = [
            new MavenIdAndRepo(new MavenId('org.swinglabs:swingx-core:1.6.2-2'), new UserBintrayRepo('bintray/jcenter')),
            new MavenIdAndRepo(new MavenId('msv:isorelax:20050913'), new UserBintrayRepo('bintray/jcenter')),
    ]

    // why 'json.jar' was ignored ?
    public static List<String> ignoreFiles = ['log4j.jar',
                                              'jdkAnnotations.jar', 'javac2.jar', 'resources.jar', 'resources_en.jar',
                                              'trove4j.jar', 'trang-core.jar', 'microba.jar', 'jdom.jar',
                                              'icons.jar', 'java_resources_en.jar', 'wadl-core.jar',
                                              'jna.jar', 'servlet-api.jar', 'okhttp.jar', 'http-client.jar',
                                              '', '', '', '', '', '', '', '', '',];

    public static List<String> startwithIgnore = ['oro-', 'netty-', 'nanoxml-', 'miglayout-', 'jsch-', 'jaxb-', 'batik-', 'netty-', 'cli-',
                                                  'asm-', 'trilead-', 'purejavacomm-', 'pty4j-', 'protobuf-', 'txw2-', 'debugger-memory-agent-', 'stax-ex-',
                                                  'snakeyaml-', 'rngom-', 'proxy-vole-', 'minlog-', 'dbus-', 'swingx-core-',
                                                  'markdownj-core-', 'jediterm-', 'jaxen-', 'jbcrypt-', 'xmlrpc-', 'winp', 'java-utils-',
                                                  'jackson-databind-', 'istack-commons-runtime-', 'isorelax-', 'ion-java-',
                                                  'ini4j-', 'imageio-', 'httpmime-', 'fluent-hc-', 'FastInfoset-', 'delight-rhino-sandbox-',
                                                  'commons-', 'streamex-', 'common-', 'aapt-proto-', 'assertj-core-', 'automaton-',
                                                  'guava-', 'gson-', 'nosyncbuilder-', 'protos-', 'xercesImpl-', 'aapt2-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-',]

    public static List addMavenIds = [
            KotlinMavenIds.reflect, KotlinMavenIds.stdlib, KotlinMavenIds.stdlib_jdk8,
            LatestMavenIds.jdom,
            LatestMavenIds.trove4jIdea,
            LatestMavenIds.httpClient,
    ]


    public List<File> unknownFiles = []

    public static List<String> ignoreMavenGroupIds = [
            'org.ow2.asm',
            'org.slf4j',
            'stax',
            'xerces',
            'xml-apis',
            'xml-resolver',
            'org.javassist',
            'org.hamcrest',
//            'org  .eclipse.jdt',
//            'org.eclipse.xtend',
//            'org.eclipse.xtext',
            'org.codehaus.groovy',
            'org.apache.velocity',
            'org.apache.ws.xmlrpc',
            'org.codehaus.plexus',
            'org.glassfish.jaxb',
            'org.jetbrains.jediterm',
            'org.jetbrains.pty4j',
            'com.sun.activation',
            'javax.xml.bind',
            'com.sun.activation',
            'javax.annotation',
            '',
            //'org.apache.httpcomponents',
    ]

    public ClassPathCalculatorGroovyWithDownloadWise mavenIdSearcher = new ClassPathCalculatorGroovyWithDownloadWise(new LongTaskInfo());
    public AllMavenIdsRefs listOfAllEnums = new AllMavenIdsRefs();
    public ClassPathCalculatorEnumConverter mavenId2EnumConverter;
    public String generatedClassNameIdea = 'ideaRuntimeGen'
    public File ideaDir1;

    @Deprecated
    IdeaRuntimeClasspath() {

    }

    IdeaRuntimeClasspath(File ideaDir1) {
        this.ideaDir1 = ideaDir1
    }

    void doAllStuff(File ideaDir) {
        ideaDir1 = ideaDir;
    }

    void doAllStuff() {
        addIdeaFiles(ideaDir1, mavenIdSearcher.addFilesToClassLoaderGroovySave);
        mavenIdSearcher.calcAndSave();
        createMavenId2EnumConverter()
        mavenId2EnumConverter.filesAndMavenIds.addAll(filterFiles(mavenIdSearcher.filesAndMavenIds));
        mavenId2EnumConverter.calcClassPathFromFiles12();
        mavenId2EnumConverter.sortElements()
//        List filesAndMavenIds = mavenId2EnumConverter.filesAndMavenIds
//        Collections.sort(filesAndMavenIds, new ObjectStringComparator())
//        mavenId2EnumConverter.filesAndMavenIds = filesAndMavenIds
    }

    void createMavenId2EnumConverter() {
        mavenId2EnumConverter = new ClassPathCalculatorEnumConverter(listOfAllEnums) {
            @Override
            Writer4Sub createWriter2() {
                Writer4Sub writer4Sub = super.createWriter2()
                writer4Sub.classNameGenerated = generatedClassNameIdea
                return writer4Sub;
            }
        }
        mavenId2EnumConverter.objectWriter
    }

    List filterFiles(List files) {
        files = files.collect { filterOnEntity1(it) }
        files = files.collect { filterOnEntity2(it) }
        return files;
    }

    Object filterOnEntity1(Object obj) {
        if (obj instanceof File) {
            String name1 = obj.getName()
            if (name1.startsWith('kotlin-')) {

                name1 = name1.replace('.jar', '')
//                name1.re
                KotlinMavenIds kotlinMavenId1 = KotlinMavenIds.all.find { it.m.artifactId == name1 }
                if (kotlinMavenId1 != null) {
                    return kotlinMavenId1;
                }
                if(ideaDir1.child('lib').isChildFile(obj)){
                    return null;
                }
            }
        }
        return obj;
    }

    Object filterOnEntity2(Object obj) {
        if (obj instanceof File) {
            if (needAcceptFile(obj, obj.getName())) {
                return obj
            } else {
                return null
            }
        }
        if (obj instanceof MavenId) {
            if (ignoreMavenGroupIds.contains(obj.groupId)) {
                return null;
            }
            return obj;
        }
        return obj
    }


    boolean needAcceptFile(File file, String name) {
        String acceptt = startwithAccept.find { name.startsWith(it) }
        if (acceptt != null) {
            return true;
        }
        if (acceptFiles.contains(name)) {
            return true;
        }
        if (ignoreFiles.contains(name)) {
            return false;
        }
        String ignorrr = startwithIgnore.find { name.startsWith(it) }
        if (ignorrr != null) {
            return false;
        }
        return onUnknownFile(file);
    }

    boolean onUnknownFile(File f) {
        unknownFiles.add(f)
        return true
    }

    static void addIdeaFiles(File ideaDir, AddFilesToClassLoaderGroovy adder) {
        IdeaPluginCompiler.addIdeaCp(adder,ideaDir)
    }


}
