package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import java.util.logging.Logger;

@CompileStatic
class JavaVersionChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static boolean javaVersionChecked = false
    public static String goodJavaVersion = '1.8'

    static void checkJavaVersion() {
        if (!javaVersionChecked) {
            javaVersionChecked = true;
            checkJavaVersionImpl();
        }
    }

    static boolean checkJavaVersionImpl() {
        String java_version = System.getProperty("java.specification.version")
        if (java_version != goodJavaVersion) {
            log.severe "bad java version : ${java_version}, needed : ${goodJavaVersion}"
            return false
        }
        return true
    }

}
