package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LangUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String[] getLibPathSystem(){
        String[] res = JrrClassUtils.getFieldValue(ClassLoader,'sys_paths') as String[];
        return res
    }

    String[] getLibPathUser(){
        String[] res = JrrClassUtils.getFieldValue(ClassLoader,'usr_paths') as String[];
        if(res==null){
            log.info "lib not loaded"
        }
        return res
    }

    void setLibToUserPathUnsafe(List<String> path2){
        String[] array = path2.toArray(new String[0])
        JrrClassUtils.setFieldValue(ClassLoader,'usr_paths',array)
    }

    void addLibToUserPath(File path2){
        assert path2.isDirectory()
        List<String> list = getLibPathUser().toList()
        list.add(path2.absolutePath)
        setLibToUserPathUnsafe(list)
    }

    void insertLibToUserPath(File path2){
        assert path2.isDirectory()
        List<String> list = getLibPathUser().toList()
        list.add(0,path2.absolutePath)
        setLibToUserPathUnsafe(list)

    }

    // java.lang.ClassLoader.NativeLibrary is element
    Vector getSystemNativeLibraries(){
        Vector res = JrrClassUtils.getFieldValue(ClassLoader,'systemNativeLibraries') as Vector;
        return res
    }

    Vector<String> getSystemNativeLoadedLibraries(){
        Vector<String> res = JrrClassUtils.getFieldValue(ClassLoader,'loadedLibraryNames') as Vector;
        return res
    }

}
