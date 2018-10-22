package net.sf.jremoterun.utilities.nonjdk.idea.init

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2

import java.util.logging.Logger

@CompileStatic
public class IdeaClasspathAdd implements GroovyObject{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean inited = false;

    public static volatile Thread initThread;

    public static PluginClassLoader pluginClassLoader = (PluginClassLoader) JrrClassUtils.getCurrentClassLoader();

    public static List<Integer> traceFlags = [];
    public static File ideaInitScriptDebug;

    public static AddFilesToClassLoaderGroovyIdea addCl = new AddFilesToClassLoaderGroovyIdea();

    static void addFileToClassLoader(File file){
        pluginClassLoader.addURL(file.toURL());
    }

    public static void init() throws Exception {
        inited = true;
        traceFlags.add  71;
        SharedObjectsUtils.getClassLoaders().put(IdeaClassPathSettings.pluginCLassloaderId, pluginClassLoader);
        traceFlags.add  72;
        SimpleFindParentClassLoader.setDefaultClassLoader(pluginClassLoader);
        traceFlags.add  73;
        log.info("classes added fine");
        initThread = Thread.currentThread();
        SetConsoleOut2.setConsoleOutIfNotInited();
        traceFlags.add  74;
        runScriptInUserDir()
        traceFlags.add  75;
        runCustom()
        traceFlags.add  76;
    }

    static void runScriptInUserDir() {
        if(JrrStarterVariables.filesDir==null){
            traceFlags.add  1;
            log.severe "files dir is null"
            JrrUtilities.showException("files dir is null",new Exception("files dir is null"));
        }else{
            ideaInitScriptDebug = new File(JrrStarterVariables.filesDir,IdeaClassPathSettings.ideaInitGroovyScriptName);
            if(ideaInitScriptDebug.exists()){
                traceFlags.add 2;
                if(JrrStarterVariables.classesDir!=null){
                    traceFlags.add 3;
                    addCl.addF JrrStarterVariables.classesDir
                }
                log.info("running ${ideaInitScriptDebug} ..");
                Script parse = GroovyMethodRunner.groovyScriptRunner.groovyShell.parse(ideaInitScriptDebug)
                parse.run()
                log.info("finished ${ideaInitScriptDebug}");
            }else{
                log.severe "file not exist : ${ideaInitScriptDebug}"
                traceFlags.add 4;
                JrrUtilities.showException("file not exist : ${ideaInitScriptDebug}",new Exception("file not exist : ${ideaInitScriptDebug}"));
            }
            traceFlags.add 5;
        }
    }


    static void runCustom() {
        traceFlags.add 6;
        String customScript = System.getProperty(IdeaClassPathSettings.customScriptProperty)
        if (customScript == null) {
            traceFlags.add 7;
            log.info("property not set : ${IdeaClassPathSettings.customScriptProperty}");
        }else{
            traceFlags.add 8;
            File customScriptF = customScript as File
            if (customScriptF.exists()) {
                traceFlags.add 9;
                log.info("running ${customScriptF} ..");
                Script parse = GroovyMethodRunner.groovyScriptRunner.groovyShell.parse(customScriptF)
                parse.run()
                log.info("finished ${customScriptF}");
            }else{
                traceFlags.add 10;
                log.info "File not found ${customScript}"
                JrrUtilities.showException("File not found ${customScript}",new FileNotFoundException("${customScript}"))
            }
            traceFlags.add 11;
        }
    }



}
