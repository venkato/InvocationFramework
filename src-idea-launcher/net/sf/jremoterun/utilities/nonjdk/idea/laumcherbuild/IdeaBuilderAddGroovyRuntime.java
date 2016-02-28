package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class IdeaBuilderAddGroovyRuntime {

    private static final Logger log = Logger.getLogger(IdeaBuilderAddGroovyRuntime.class.getName());

//    static File jrrPathDefault = new File("c:/Users/nick/git/starter");

    public static URLClassLoader groovyCl;

    static void f1() throws Exception {
        log.info("starting ...");
        String jrrpath = System.getProperty("jrrpath");
        log.info("jrrpath = " + jrrpath);
        if (jrrpath == null) {
            log.severe("jrrpath is null ");
            throw new Exception("jrrpath is null ");
        }
//        jrrpath = jrrPathDefault.getAbsolutePath();
        File f = new File(jrrpath);
        if (!f.exists()) {
            throw new FileNotFoundException(f.getAbsolutePath());
        }
        log.info("cp3");
        f2(f);
        log.info("finished fine");

    }

    static void f2(File jrrpath) throws Exception {
        URLClassLoader classLoader = (URLClassLoader) IdeaBuilderAddGroovyRuntime.class.getClassLoader();
        File copyDir = new File(jrrpath, "libs/copy");
        File f2 = new File(copyDir, "groovy_custom.jar");
        File f3 = new File(copyDir, "groovy.jar");
        addUrlToCl(classLoader, f2);
        addUrlToCl(classLoader, f3);
        log.info("creating groovy cl");
        Class<?> aClass = classLoader.loadClass("groovy.lang.GroovyClassLoader");
        Constructor<?> constructor = aClass.getConstructor(ClassLoader.class);
        groovyCl = (URLClassLoader) constructor.newInstance(classLoader);

        addUrlToCl(groovyCl, new File(copyDir, "jremoterun.jar"));
        addUrlToCl(groovyCl, new File(copyDir, "jrrassist.jar"));
        addUrlToCl(groovyCl, new File(jrrpath, "JrrInit/src"));
        if (true) {
            addUrlToCl(groovyCl, new File(jrrpath, "onejar/jrrutilities.jar "));
        } else {
            addUrlToCl(groovyCl, new File(jrrpath, "JrrUtilities/src"));
            addUrlToCl(groovyCl, new File(jrrpath, "JrrStarter/src"));
        }
        log.info("jars added to groovy cl");
        Thread.currentThread().setContextClassLoader(groovyCl);
        Class<?> aClass1 = groovyCl.loadClass("net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBRunnerImpl");
        Runnable o = (Runnable) aClass1.newInstance();
        log.info("running " + aClass1);
        o.run();
    }


    static Method addUrlM;

    static void addUrlToCl(URLClassLoader cl, File jrrpath) throws Exception {
        log.info("adding to CL : " + jrrpath);
        if (!jrrpath.exists()) {
            throw new FileNotFoundException(jrrpath.getAbsolutePath());
        }
        if (addUrlM == null) {
            addUrlM = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlM.setAccessible(true);
        }
        addUrlM.invoke(cl, jrrpath.toURL());
    }

}
