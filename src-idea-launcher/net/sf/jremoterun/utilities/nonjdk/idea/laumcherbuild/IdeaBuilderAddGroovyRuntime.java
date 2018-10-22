package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import org.jetbrains.jps.cmdline.LauncherOriginal;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.logging.Logger;

public class IdeaBuilderAddGroovyRuntime {

    private static final Logger log = Logger.getLogger(IdeaBuilderAddGroovyRuntime.class.getName());


    public static void f1() throws Exception {
        try {
            if(IdeaBuildRunnerSettings.redirectOutToFileAux){
                doRedirect();
            }
            if(IdeaBuildRunnerSettings.jrrIdeaForceUseStd){
                log.info("force use LauncherOriginal");
                runOriginal();
            }else {
                doJobImpl();
            }
        }catch (Throwable e){
            e.printStackTrace();
            if(IdeaBuildRunnerSettings.startOriginal){
                if(IdeaBuildRunnerSettings.originalTried){
                    throw e;
                }
                runOriginal();
            }
        }
    }


    public static void doRedirect() throws Exception {

        //FileOutputStream fous = new FileOutputStream(jrrlibpath);
        PrintStream printStream1= new PrintStream(IdeaBuildRunnerSettings.buildLogBefore);
        IdeaBuildRunnerSettings.jrrOutStream = printStream1;
        System.setOut(printStream1);
        System.setErr(printStream1);
        System.out.println("starting "+new Date());
    }

    public static void doJobImpl() throws Exception {
        log.info("starting ...");
        IdeaBuildRunnerSettings.jrrpathF = detectJrrPath();
        log.info("jrrpath = " + IdeaBuildRunnerSettings.jrrpathF);
        if (IdeaBuildRunnerSettings.jrrpathF == null) {
            log.severe("jrrpath is null ");
            if(IdeaBuildRunnerSettings.startOriginal){
                runOriginal();
            }else {
                throw new Exception("jrrpath is null ");
            }
        }else {
//        jrrpath = jrrPathDefault.getAbsolutePath();
            if (!IdeaBuildRunnerSettings.jrrpathF.exists()) {
                throw new FileNotFoundException(IdeaBuildRunnerSettings.jrrpathF.getAbsolutePath());
            }
            log.info("cp3");
            f2(IdeaBuildRunnerSettings.jrrpathF);
            log.info("finished fine");
        }
    }

    static void runOriginal() throws MalformedURLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        LauncherOriginal.main(IdeaBuildRunnerSettings.argsPv2.toArray(new String[0]));
    }

    public static File detectJrrPath() throws Exception {
        String jrrpath = System.getProperty(IdeaBuildRunnerSettings.jrrpathS);
        log.info("jrrpath sys prop = " + jrrpath);
        if(jrrpath!=null){
            File f = new File(jrrpath);
            if(!f.exists()){
                throw new FileNotFoundException(f.getAbsolutePath());
            }
            return f;
        }
        boolean jrrlibpathE = IdeaBuildRunnerSettings.jrrlibpathF.exists();
        log.info("jrrlibpath exit : "+jrrlibpathE+" "+IdeaBuildRunnerSettings.jrrlibpathF);
        if(jrrlibpathE){
            FileInputStream fis = new FileInputStream(IdeaBuildRunnerSettings.jrrlibpathF);
            try{
                byte[] buff= new byte[10000];
                int read = fis.read(buff);
                if(read<2){
                    throw new IOException("Failed read : "+IdeaBuildRunnerSettings.jrrlibpathF.getAbsolutePath());
                }
                String pathFromFileS = new String(buff, 0, read).trim();
                log.info("jrr path from file : "+pathFromFileS);
                File jrrLibPath2 = new File(pathFromFileS);
                if(!jrrLibPath2.exists()){
                    throw new FileNotFoundException(jrrLibPath2.getAbsolutePath());
                }
                return jrrLibPath2;
            }finally {
                fis.close();
            }
        }
        return null;
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
        IdeaBuildRunnerSettings.groovyCl = (URLClassLoader) constructor.newInstance(classLoader);

        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(copyDir, "jremoterun.jar"));
        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(copyDir, "jrrassist.jar"));
        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrInit/src"));
        if (IdeaBuildRunnerSettings.useOneJar) {
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "onejar/jrrutilities.jar"));
        } else {
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrUtilities/src"));
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrStarter/src"));
        }
        log.info("jars added to groovy cl");
        Thread.currentThread().setContextClassLoader(IdeaBuildRunnerSettings.groovyCl);
        Class<?> aClass1 = IdeaBuildRunnerSettings.groovyCl.loadClass("net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBRunnerImpl");
        Runnable o = (Runnable) aClass1.newInstance();
        log.info("running " + aClass1);
        o.run();
    }


    public static Method addUrlM;

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
