package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class GroovyCompilerParams implements Serializable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<File> files = [];
    List<File> dirs = [];
    File outputDir;
    String javaVersion = '1.6';//System.getProperty('java.specification.version')
    List<String> testClassLoaded = []
    List<String> testClassLoadedSameClassLoader = []
    List<String> additionalFlags = []
    boolean eclipseCompiler = true;
    boolean printWarning = false;

    GroovyCompilerParams() {
        if(javaVersion==null){
            javaVersion = '1.8'
        }

    }

    void addInDir(File... dirs2) {
        assert dirs2 != null
        for (int i = 0; i < dirs2.length; i++) {
            File dir = dirs2[i];
            assert dir.exists()
            dirs.add(dir)
        }
    }

    void addInFile(File file) {
        assert file.exists()
        files.add(file)
    }

    void addTestClassLoadedSameClassLoader(Class clazz) {
        testClassLoadedSameClassLoader.add(clazz.name)
    }

    void addTestClassLoaded(Class clazz) {
        testClassLoaded.add(clazz.name)
    }

}
