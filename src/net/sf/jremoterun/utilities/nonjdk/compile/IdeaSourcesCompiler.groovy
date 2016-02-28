package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class IdeaSourcesCompiler extends IfFrameworkCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<IfFrameworkSrcDirs> dirs5 = [//
                                                    IfFrameworkSrcDirs.src_frameworkloader,
                                                    IfFrameworkSrcDirs.src_groovycompiler,
                                                    IfFrameworkSrcDirs.src_idea,
                                                    IfFrameworkSrcDirs.src_common,
                                                    IfFrameworkSrcDirs.src_logger_ext_methods,
    ]

    public static List mavenIds = [
            CustObjMavenIds.git,
            LatestMavenIds.jsoup,
            LatestMavenIds.jodaTime,
    ]


    File baseDir

    IdeaSourcesCompiler(File baseDir) {
        this.baseDir = baseDir
    }

    static void addIdeaStuff(File ideaDir, AddFilesToClassLoaderCommon adder) {
        adder.addAllJarsInDir new File(ideaDir, "lib/")
        adder.addAllJarsInDir new File(ideaDir, 'plugins/Groovy/lib/');
    }

    void prepare() {
        client.adder.addFileWhereClassLocated(JrrUtilities)
        client.adder.addFileWhereClassLocated(DropshipClasspath)
        client.adder.addAll DropshipClasspath.allLibsWithoutGroovy
        client.adder.addAll mavenIds
        client.adder.add mcu.getToolsJarFile()


        params.printWarning = false;
        params.addInDir new File(baseDir, "src")
        params.outputDir = new File(client.ifDir, 'build/ideapluginbuild')
        params.outputDir.mkdirs()
        params.javaVersion = '1.6'
        dirs5.each {
            addInDir(client.ifDir.child(it.dirName))
        }
        addInDir client.ifDir.child(IfFrameworkResourceDirs.resources_groovy.dirName)

    }


}

// net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaGroovyStarter