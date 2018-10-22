package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class NativeLibraryLoaderChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void loadLibrary(List<File> pathToFiles, String libName) {
        pathToFiles.each {
            new LangUtils().insertLibToUserPath(it)
        }
        Runtime.getRuntime().loadLibrary(libName)
    }

    /**
     * Just libname
     */
    void loadLibrary(String libName) {
        Runtime.getRuntime().loadLibrary(libName)
    }

    void loadByAbsolutPath(File pathToLib){
        assert pathToLib.exists();
        Runtime.getRuntime().load(pathToLib.getAbsolutePath())
    }


}
