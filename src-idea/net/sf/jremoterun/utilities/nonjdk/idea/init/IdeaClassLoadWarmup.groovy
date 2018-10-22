package net.sf.jremoterun.utilities.nonjdk.idea.init

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class IdeaClassLoadWarmup {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static  List<String> tryLoadClasses = []
    public static volatile int lastIdused =1;
    public static ClassLoader classLoaderToWarmup = IdeaClassLoadWarmup.getClassLoader()

    static void initideacl() {
        lastIdused++;
        initideacl2(lastIdused);
    }

    static void initideacl2(int id) {
        try {
            ClRef jfeeObject = new ClRef('org.jfree.data.ComparableObjectItem')
            tryLoadClasses.add(jfeeObject.className)
            jfeeObject.loadClass(classLoaderToWarmup)
        } catch (Throwable e) {
            log.info "${e}"
        }

        try {
            String className2 =  "nosuchpackage.random.packa${id}.nusuchfile${id}"
            tryLoadClasses.add(className2)
            classLoaderToWarmup.loadClass(className2)
        } catch (Throwable e) {
        }
    }


}
