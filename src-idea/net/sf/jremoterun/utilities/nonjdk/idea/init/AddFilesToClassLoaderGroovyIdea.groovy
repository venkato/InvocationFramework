package net.sf.jremoterun.utilities.nonjdk.idea.init

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy;

import java.util.logging.Logger;

@CompileStatic
class AddFilesToClassLoaderGroovyIdea extends AddFilesToClassLoaderGroovy{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void addFileImpl(File file) throws Exception {
        IdeaClasspathAdd.addFileToClassLoader(file)
    }
}
