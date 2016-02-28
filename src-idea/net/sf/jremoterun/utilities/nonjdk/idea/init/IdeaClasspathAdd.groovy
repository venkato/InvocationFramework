package net.sf.jremoterun.utilities.nonjdk.idea.init

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2

import java.util.logging.Logger

@CompileStatic
public class IdeaClasspathAdd {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean inited = false;

    public static volatile Thread initThread;

    public static PluginClassLoader pluginClassLoader = (PluginClassLoader) JrrClassUtils.getCurrentClassLoader();

    public static AddFilesToClassLoaderGroovy addCl = new AddFilesToClassLoaderGroovy() {
        @Override
        public void addFileImpl(File file) throws Exception {
            addFileToClassLoader(file)
        }
    };

    static void addFileToClassLoader(File file){
        pluginClassLoader.addURL(file.toURL());
    }

    public static void init() throws Exception {
        inited = true;
        SharedObjectsUtils.getClassLoaders().put(IdeaClassPathSettings.pluginCLassloaderId, pluginClassLoader);
        SimpleFindParentClassLoader.setDefaultClassLoader(pluginClassLoader);
        log.info("classes added fine");
        initThread = Thread.currentThread();
        SetConsoleOut2.setConsoleOutIfNotInited();
        runScriptInUserDir()
        runCustom()
    }

    static void runScriptInUserDir() {
        File userHome = System.getProperty('user.home') as File
        if (userHome.exists()) {
            File scriptInUserDir = new File(userHome, 'jrr/configs/idea.groovy')
            if (scriptInUserDir.exists()) {
                log.info("running ${scriptInUserDir} ..");
                Script parse = GroovyMethodRunner.groovyScriptRunner.groovyShell.parse(scriptInUserDir)
                parse.run()
                log.info("finished ${scriptInUserDir}");
            }else{
                log.info "file not exist : ${scriptInUserDir}"
            }
        }else{
            log.info "user home not exist : ${userHome}"
        }
    }


    static void runCustom() {
        String customScript = System.getProperty(IdeaClassPathSettings.customScriptProperty)
        if (customScript == null) {
            log.info("property not set : ${IdeaClassPathSettings.customScriptProperty}");
        }else{
            File customScriptF = customScript as File
            if (customScriptF.exists()) {
                log.info("running ${customScriptF} ..");
                Script parse = GroovyMethodRunner.groovyScriptRunner.groovyShell.parse(customScriptF)
                parse.run()
                log.info("finished ${customScriptF}");
            }else{
                log.info "File not found ${customScript}"
                JrrUtilities.showException("File not found ${customScript}",new FileNotFoundException("${customScript}"))
            }
        }
    }



}
