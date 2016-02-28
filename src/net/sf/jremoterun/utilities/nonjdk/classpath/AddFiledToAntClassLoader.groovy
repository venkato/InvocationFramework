package net.sf.jremoterun.utilities.nonjdk.classpath;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import org.apache.tools.ant.AntClassLoader;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AddFiledToAntClassLoader extends AddFilesToClassLoaderGroovy{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AntClassLoader antClassLoader

    AddFiledToAntClassLoader(AntClassLoader antClassLoader) {
        this.antClassLoader = antClassLoader
    }

    @Override
    void addFileImpl(File file) throws Exception {
        antClassLoader.addPathComponent(file)
    }
}
