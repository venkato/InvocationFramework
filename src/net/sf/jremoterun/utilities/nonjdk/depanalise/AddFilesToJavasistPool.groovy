package net.sf.jremoterun.utilities.nonjdk.depanalise

import groovy.transform.CompileStatic
import javassist.ClassPool
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy

import java.util.logging.Logger

@CompileStatic
class AddFilesToJavasistPool extends AddFilesToClassLoaderGroovy {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassPool classPool;

    AddFilesToJavasistPool() {
        classPool = ClassPool.getDefault();
    }

    AddFilesToJavasistPool(ClassPool classPool) {
        this.classPool = classPool
    }

    @Override
    void addFileImpl(File file) throws Exception {
        classPool.appendClassPath(file.absolutePath);
    }


}
