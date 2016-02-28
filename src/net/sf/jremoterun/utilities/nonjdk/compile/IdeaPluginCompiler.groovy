package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrConfigGenerator

import java.util.logging.Logger

@CompileStatic
class IdeaPluginCompiler extends IfFrameworkCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    IdeaPluginCompiler() {
    }

    static void addIdeaStuff(File ideaDir, AddFilesToClassLoaderCommon adder) {
        adder.addAllJarsInDir new File(ideaDir, "lib/")
        adder.addAllJarsInDir new File(ideaDir, 'plugins/Groovy/lib/');
    }

    void compile3(File ideaDir) {
        prepare()
        prepare2(ideaDir)
        compile()
        postCompileStep()
    }


    File getCompiledDir() {
        params.outputDir = new File(client.ifDir, 'build/ideaAndIf')
    }




    void prepare2(File ideaDir) {
        assert ideaDir.exists()
        client.adder.addAllJarsInDir new File(ideaDir, "lib/")
        client.adder.addAllJarsInDir new File(ideaDir, 'plugins/Groovy/lib/');
        params.outputDir = getCompiledDir()
        params.outputDir.mkdirs()
        params.javaVersion = '1.8'
        if (InfocationFrameworkStructure.ifDir == null) {
            InfocationFrameworkStructure.ifDir = client.ifDir
        }
        IfFrameworkSrcDirs.dir2.each { addInDir(it) }
        IfFrameworkSrcDirs.idea.each { addInDir(it) }
        client.adder.add IdeaInitPluginCompiler.getJrrUtilsJar()
        client.adder.addFileWhereClassLocated(JrrUtilities)
        client.adder.addFileWhereClassLocated(DropshipClasspath)
    }


}

// net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaGroovyStarter