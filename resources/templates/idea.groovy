import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

@CompileStatic
class IdeaInitInHomeDir3 extends GroovyRunnerConfigurator2 {

    ClRef cnr1 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init2.IdeaInit3')


    File gitRepo = "FgitRepoF" as File
    File ideaLogDir = "FideaLogDirF" as File
    File compiledClasses = "FcompiledClassesF" as File


    AddFilesToClassLoaderGroovy adder2 = IdeaClasspathAdd.addCl
    PluginClassLoader cl = IdeaClasspathAdd.pluginClassLoader;

    @Override
    void doConfig() {
        println('IdeaInitInHomeDir3 : in idea.groovy')
        adder2.isLogFileAlreadyAdded = false
        adder2.add compiledClasses
        println('IdeaInitInHomeDir3 : compiled classes added')
        RunnableWithParamsFactory.fromClass3(cnr1, cl, [gitRepo, ideaLogDir])
        println(' idea.groovy finished')
    }
}





