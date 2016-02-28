package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import groovy.lang.GroovyClassLoader;

import java.io.File;
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
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) IdeaBuilderAddGroovyRuntime.groovyCl;
        File f = new File(System.getProperty("user.home")+"/jrr/configs/idea_builder.groovy");
        log.info("loading config : "+f);
        Class aClass = groovyClassLoader.parseClass(f);
        Runnable instance = (Runnable) aClass.newInstance();
        log.info("running : "+aClass);
        instance.run();
        log.info("cp7");
    }

}
