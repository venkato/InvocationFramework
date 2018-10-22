package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compile.IdeaInitPluginCompiler
import net.sf.jremoterun.utilities.nonjdk.compile.IdeaPluginCompiler
import net.sf.jremoterun.utilities.nonjdk.compile.JrrUtilsCompiler
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
class JrrIdeaGenerator implements ClassNameSynonym {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String pluginName = 'IdeaJarLoader'

    void compileInitAndUpdate(File ideaDir, File ideaPLuginDir) {
        File ideaPlugin = compileAndPrepare(ideaDir)
        File ideaPluginConfigs = ideaPLuginDir.child('config/plugins')
        assert ideaPLuginDir.exists()
        FileUtilsJrr.copyDirectoryToDirectory(ideaPlugin, ideaPluginConfigs)
        updateVmOptions(ideaPLuginDir)
        log.info "plugin created in ${ideaPluginConfigs}"
    }

    File compileAndPrepare(File ideaDir) {
        new JrrUtilsCompiler().all2()
        IdeaInitPluginCompiler compiler = new IdeaInitPluginCompiler()
        compiler.compile3(ideaDir)
        File pluginDir2 = compiler.client.ifDir.child("build/${pluginName}")
        File pluginDir = pluginDir2.child("lib")
        pluginDir.mkdirs()
        FileUtilsJrr.copyFileToDirectory(JrrStarterJarRefs2.jremoterun.resolveToFile(), pluginDir)
        FileUtilsJrr.copyFileToDirectory(JrrStarterJarRefs2.jrrassist.resolveToFile(), pluginDir)
        FileUtilsJrr.copyFileToDirectory(IdeaInitPluginCompiler.getJrrUtilsJar(), pluginDir)
        File pluginJar = pluginDir.child("${pluginName}.jar")
        File metaInf = compiler.client.ifDir.child('resources/idea/META-INF')
        compiler.testUpdateIdeaJar2(pluginJar, metaInf)
        return pluginDir2
    }

    void compileAndGenerateIdeaConfig2(File ideaDir, File ideaLogDir) {
        compile(ideaDir)
        generateIdeaConfig2(ideaLogDir)
    }

    void compile(File ideaDir){
        new IdeaPluginCompiler().compile3(ideaDir)
    }

    void generateIdeaConfig2(File ideaLogDir) {
        generateIdeaConfig(getGitRepo(), ideaLogDir, new IdeaPluginCompiler().getCompiledDir())
    }

    static File getGitRepo(){
        CustomObjectHandlerImpl handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl
        File gitRepo = handler.cloneGitRepo3.gitBaseDir
        return gitRepo
    }

    void updateVmOptions( File ideaPLuginDir){
        File ideaVmOptions = ideaPLuginDir.child('config/idea64.exe.vmoptions')
        assert ideaVmOptions.exists()
        String text = ideaVmOptions.text
        if(!text.contains('jremoterun')){
            File f = GroovyMethodRunnerParams.instance.grHome.child('libs/copy/jremoterun.jar')
            assert f.exists()
            String path = f.absolutePath.replace('\\', '/')
            String s = "-javaagent:${path}\n"
            text+=s
            ideaVmOptions.text = text
        }
    }


    void generateIdeaConfig(File gitRepo, File ideaLogDir, File compiledClasses) {
        String s = JrrConfigGenerator.readText('idea.groovy')
        s = replace2(s, 'FgitRepoF', gitRepo)
        s = replace2(s, 'FideaLogDirF', ideaLogDir)
        s = replace2(s, 'FcompiledClassesF', compiledClasses)
        File jrrConfigDir2 = JrrConfigGenerator.getJrrConfigDir()
        jrrConfigDir2.child('idea.groovy').text = s
    }

    String replace2(String text, String key, File f) {
        String filePath = f.absolutePath.replace('\\', '/')
        assert text.contains(key)
        return text.replace(key, filePath)
    }

}
