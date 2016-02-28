package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.MethodNode

import java.util.logging.Logger

@CompileStatic
class ExtentionMethodsChecker implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<String, List<MethodNode>> cachedMethods

    @Override
    void run() {
        check1()
    }

    static void findCachedMethods() {
        Object obj = JrrClassUtils.getFieldValue(org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport, "EXTENSION_METHOD_CACHE")
        cachedMethods = (Map) JrrClassUtils.getFieldValue(obj, "cachedMethods")
        if (cachedMethods == null) {
            throw new IllegalStateException("cachedMethods was not initialized")
        }
    }

    static void check1() {
        List<MethodNode> methodsLogger = getMethods(Logger)
        if (methodsLogger.find { it.name == 'loge' } == null) {
            throw new Exception("failed find extension method loge for class Logger")
        }
        List<MethodNode> methodsFile = getMethods(File)
        if (methodsFile.find { it.name == 'child' } == null) {
            throw new Exception("failed find extension method child for class File")
        }
    }

    static List<MethodNode> getMethods(Class clazz) {
        if (cachedMethods == null) {
            findCachedMethods()
        }
        List<MethodNode> methodNodes = cachedMethods.get(clazz.name)
        if (methodNodes == null) {
            throw new IllegalStateException("Failed find extention methods for class : ${clazz.name}")
        }
        return methodNodes

    }

}
