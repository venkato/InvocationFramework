package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorI
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorIThrowImmediate
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemFoundException
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemHelper

import java.util.logging.Logger

@Deprecated
@CompileStatic
public abstract class ClassPathTesterHelper extends ClassPathTesterHelper2 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    ClassLoader currentClassLoader = JrrClassUtils.getCurrentClassLoader()

    static MavenCommonUtils mavenCommonUtils = new MavenCommonUtils();

    ClassPathTesterHelper(ProblemCollectorI problemCollector) {
        super(problemCollector)
    }

    ClassPathTesterHelper() {
        super(new ProblemCollectorIThrowImmediate())
    }

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    static void checkClassInstanceOf(Object object, Class instanceOf) {
        createClassPathTesterHelper2().checkClassInstanceOf5(object,instanceOf)
    }


    static void checkTheSameClassLoader(Class clazz, ClassLoader classLoader) {
        createClassPathTesterHelper2().checkTheSameClassLoader5(clazz,classLoader)
    }

    public static void checkNoSuchClass(ClRef className, ClassLoader classLoader) {
        createClassPathTesterHelper2().checkNoSuchClass5(className,classLoader)
    }

    public static void checkFieldExists(Class clazz, String field) {
        createClassPathTesterHelper2().checkFieldExists5(clazz,field)
    }


    static void checkClassOnce(Class clazz, MavenIdContains mavenId) {
        createClassPathTesterHelper2().checkClassOnce5(clazz, mavenId.getM())
    }

    static void checkClassOnce(Class clazz) {
        createClassPathTesterHelper2().checkClassOnce5(clazz)
    }

    @Deprecated
    static void checkClassNotFromPathNoEx(Class clazz, File notPath) {
        try {
            checkClassNotFromPath(clazz, notPath)
        } catch (ProblemFoundException e) {
            JrrUtilities.showException(e.getMessage(), e)
        } catch (UndesiredClassLocationException e) {
            JrrUtilities.showException(e.getMessage(), e)
        }
    }

    static void checkClassNotFromPath(Class clazz, File notPath) {
        createClassPathTesterHelper2().checkClassNotFromPath5(clazz,notPath)

    }

    static void checkClassOnce(Class clazz, File expectedFile) {
        createClassPathTesterHelper2().checkClassOnce5(clazz,expectedFile)
    }

    public static void checkClassLocation(Class clazz, MavenId mavenId) {
        createClassPathTesterHelper2().checkClassLocation5(clazz,mavenId)
    }

    public static void checkClassLocation(Class clazz, File locationExpected) {
        createClassPathTesterHelper2().checkClassLocation5(clazz,locationExpected)

    }

    public abstract void runImpl();


}
