package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class FieldResolvedDirectly {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Direct means - field is static.
     */
    public static FieldResolvedDirectly fieldResolvedDirectly = new FieldResolvedDirectly();


    public HashSet<String> directClasses = new HashSet<>()
    public HashSet<String> directEnumClasses = new HashSet<>()

    public ClassLoader classLoader = getClass().getClassLoader()




    void addDirectEnumClass(ClRef clazz){
        directEnumClasses.add(clazz.getName())
    }

    void addDirectEnumClass(Class clazz){
        directEnumClasses.add(clazz.getName())
    }

    boolean canResolveEnum(String className){
        log.info "checking can resolve enum : ${className} "
        return directEnumClasses.contains(className);
    }


    void addDirectClass(ClRef clazz){
        directClasses.add(clazz.getName())
    }

    void addDirectClass(Class clazz){
        directClasses.add(clazz.getName())
    }


    boolean canResolve(String className,String fieldName){
        log.info "checking can resolve : ${className} ${fieldName}"
        return directClasses.contains(className)
    }

    Class loadClass(String className){
        Class<?> clazz = classLoader.loadClass(className)
        return clazz
    }




    File resolveValue(String className,String fieldName){
        File file1 = resolveValue2(className,fieldName) as File
        return file1

    }


    Object resolveValue2(String className,String fieldName){
        Thread thread = Thread.currentThread();
        ClassLoader loaderBefore = thread.getContextClassLoader()
        thread.setContextClassLoader(classLoader)
        try {
            Class<?> clazz = classLoader.loadClass(className)
            return JrrClassUtils.getFieldValue(clazz, fieldName)
        }finally{
            thread.setContextClassLoader(loaderBefore)
        }

    }





}
