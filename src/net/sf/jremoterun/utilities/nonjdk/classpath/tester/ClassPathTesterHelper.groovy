package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2

import java.util.logging.Logger

@CompileStatic
public abstract class ClassPathTesterHelper implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoader currentClassLoader = ClassPathTesterHelper.getClassLoader();

    static MavenCommonUtils mavenCommonUtils = new MavenCommonUtils();

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void checkClassInstanceOf(Object object, Class instanceOf) throws Exception {
        Class<?> aClass = object.getClass();
        URL location = JrrUtils.getClassLocation(aClass);
        String msg = instanceOf.getName() + " class : " + aClass.getName() + " , location " + location;
        if (aClass != instanceOf) {
            throw new Exception(msg);
        }
//        log.info(msg);
    }

//    public void checkNoSuchClass(ClRef className) throws Exception {
//        checkNoSuchClass(className.className)
//    }

    public void checkNoSuchClass(ClRef className) throws Exception {
        checkNoSuchClass(className, currentClassLoader);
    }

    static void checkTheSameClassLoader(Class clazz, ClassLoader classLoader) {
        ClassLoader classLoader1 = clazz.getClassLoader()
        if (classLoader1 != classLoader) {
            if (classLoader1 == null) {
                throw new Exception("Class ${clazz.name} loaded by boot classloader");
            }
            throw new Exception("Class ${clazz.name} loaded by diff classloader ${classLoader1.class.name} ${classLoader1}");
        }
    }

    public static void checkNoSuchClass(ClRef className, ClassLoader classLoader) throws Exception {
        try {
            Class<?> clazz = classLoader.loadClass(className.className);
            checkTheSameClassLoader(clazz, classLoader)
            File location = UrlCLassLoaderUtils.getClassLocation(clazz);
            throw new Exception( "${className.className} is present : ${location}" );
        } catch (ClassNotFoundException e) {

        }
    }

    public static void checkFieldExists(Class clazz, String field) throws Exception {
        try {
            JrrClassUtils.findField(clazz, field);
        } catch (NoSuchFieldException e) {
            File location = UrlCLassLoaderUtils.getClassLocation(clazz);
            throw new Exception("In class " + clazz.getName() + " field not found " + field + " , location " + location);
        }
    }

    static void checkClassOnce(Class clazz, MavenIdContains  mavenId) {
        checkClassOnce(clazz, mavenCommonUtils.findMavenOrGradle(mavenId.getM()))
    }

    static void checkClassOnce(Class clazz) {
        List<File> files = UrlCLassLoaderUtils2.getClassLocationAll2(clazz);
        assert files.size()==1
    }

    static void checkClassOnce(Class clazz, File expectedFile) {
        if (expectedFile == null) {
            throw new Exception("expectedFile is null for ${clazz}")
        }
        if (!expectedFile.exists()) {
            throw new Exception("expectedFile ${expectedFile} not exists for ${clazz}")
        }
        expectedFile = expectedFile.canonicalFile.absoluteFile
        List<File> files = UrlCLassLoaderUtils2.getClassLocationAll2(clazz);
        switch (files.size()) {
            case 0:
                throw new Exception("class was not found : ${clazz.name}")
            case 1:
                File actualFile = files[0]
                if (actualFile == null) {
                    throw new Exception("actual file is null for ${clazz} ${expectedFile}")
                }
                actualFile = actualFile.canonicalFile.absoluteFile
                assert actualFile == expectedFile
                break;
            default:
                files = files.collect { it.canonicalFile }
                throw new Exception("found many path ${files.size()} : ${files.join(' , ')}")
        }
    }

    public static void checkClassLocation(Class clazz, MavenId mavenId) throws Exception {
        checkClassLocation(clazz, mavenCommonUtils.findMavenOrGradle(mavenId));
    }

    public static void checkClassLocation(Class clazz, File locationExpected) throws Exception {
        File currentLocation = UrlCLassLoaderUtils.getClassLocation(clazz);
        if (locationExpected != currentLocation) {
            throw new Exception("class " + clazz.getName() + " location strange " + currentLocation);
        }

    }

    public abstract void runImpl() throws Exception;


}
