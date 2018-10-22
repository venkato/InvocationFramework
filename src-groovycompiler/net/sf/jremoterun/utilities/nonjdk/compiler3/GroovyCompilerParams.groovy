package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class GroovyCompilerParams implements Serializable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<File> files = [];
    List<File> dirs = [];
    List<ClRef> classNameRunnerWithParams = [];
    File outputDir;
    String javaVersion = '1.6';//System.getProperty('java.specification.version')
    List<ClRef> testClassLoaded = []

    /**
     * need use custom groovy jar, where IO class was deleted. Need delete this jar as it depends on other jars, where are not in classpath
     */
    List<ClRef> testNotFoundInParentClassLoaded = [new ClRef('org.codehaus.groovy.tools.shell.IO')]
    List<ClRef> testClassLoadedSameClassLoader = []
    List<String> additionalFlags = []
    boolean eclipseCompiler = true;
    boolean printWarning = false;
    boolean addExtentionJrrMethods = true;
    boolean needDoStdChecks = true;
    boolean needCustomJrrGroovyFieldsAccessors = true
    //boolean needAddDefaultClassesToParentCl =  true

    GroovyCompilerParams() {
        if (javaVersion == null) {
            javaVersion = '1.8'
        }

    }

    void setStdGroovyCompile() {
        needCustomJrrGroovyFieldsAccessors = false
        addExtentionJrrMethods = false
    }

    void addInDir(ToFileRef2 fileRef) {
        addInDir fileRef.resolveToFile()
    }

    void addInDir(File... dirs2) {
        assert dirs2 != null
        if (dirs2.length == 0) {
            throw new Exception('dirs array has 0 size')
        }
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
        testClassLoadedSameClassLoader.add(new ClRef(clazz))
    }

    void addTestClassLoaded(Class clazz) {
        testClassLoaded.add(new ClRef(clazz))
    }

}
