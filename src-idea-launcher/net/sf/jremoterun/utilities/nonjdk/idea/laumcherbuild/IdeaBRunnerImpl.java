package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import groovy.lang.GroovyClassLoader;
import org.jetbrains.jps.cmdline.LauncherOriginal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;


public class IdeaBRunnerImpl implements Runnable {

    private static final Logger log = Logger.getLogger(IdeaBRunnerImpl.class.getName());

    @Override
    public void run() {
        try {
            f1();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void f1() throws Exception {
        log.info("cp5");
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) IdeaBuildRunnerSettings.groovyCl;
        log.info("loading config : "+IdeaBuildRunnerSettings.ideaBuilderConfigFile);
        if(IdeaBuildRunnerSettings.ideaBuilderConfigFile.exists()) {
            Class aClass = groovyClassLoader.parseClass(IdeaBuildRunnerSettings.ideaBuilderConfigFile);
            Runnable instance = (Runnable) aClass.newInstance();
            log.info("running : " + aClass);
            instance.run();
            log.info("cp7");
        }else{
            log.info("idea config file not exists : "+IdeaBuildRunnerSettings.ideaBuilderConfigFile);
            if(IdeaBuildRunnerSettings.startOriginal){
                LauncherOriginal.main(IdeaBuildRunnerSettings.argsPv2.toArray(new String[0]));
            }else{
                throw new FileNotFoundException(IdeaBuildRunnerSettings.ideaBuilderConfigFile.getAbsolutePath());
            }
        }
    }

}
