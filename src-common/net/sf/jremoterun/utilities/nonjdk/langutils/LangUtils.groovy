package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LangUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String sys_paths = 'sys_paths'
    public static String usr_paths = 'usr_paths'

    String[] getLibPathSystem() {
        String[] res = JrrClassUtils.getFieldValue(ClassLoader, sys_paths) as String[];
        return res
    }

    String[] getLibPathUser() {
        String[] res = JrrClassUtils.getFieldValue(ClassLoader, usr_paths) as String[];
        if (res == null) {
            log.info "lib not loaded"
        }
        return res
    }

    void setLibToPathUnsafe(List<String> path2, String fieldName) {
        String[] array = path2.toArray(new String[0])
        JrrClassUtils.setFieldValue(ClassLoader, fieldName, array)
    }

    void addLibToUserPath(File path2) {
        addLibToUserPath2(path2, usr_paths)
    }

    void addLibToUserPath2(File path2, String fieldName) {
        assert path2.isDirectory()
        List<String> list = getLibPathUser().toList()
        list.add(path2.absolutePath)
        setLibToPathUnsafe(list, fieldName)
    }

    void insertLibToUserPath(File path2) {
        insertLibToUserPath2(path2, usr_paths)
    }

    void insertLibToUserPath2(File path2, String fieldName) {
        assert path2.isDirectory()
        List<String> list = getLibPathUser().toList()
        list.add(0, path2.absolutePath)
        setLibToPathUnsafe(list, fieldName)

    }

    // java.lang.ClassLoader.NativeLibrary is element
    Vector getSystemNativeLibraries() {
        Vector res = JrrClassUtils.getFieldValue(ClassLoader, 'systemNativeLibraries') as Vector;
        return res
    }

    Vector<String> getSystemNativeLoadedLibraries() {
        Vector<String> res = JrrClassUtils.getFieldValue(ClassLoader, 'loadedLibraryNames') as Vector;
        return res
    }

    static void loadLibrary(Class<?> fromClass, String libName, boolean isAbsolute) {
        JrrClassUtils.invokeJavaMethod(ClassLoader, 'loadLibrary', fromClass, libName, isAbsolute);
    }

}
