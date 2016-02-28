package net.sf.jremoterun.utilities.nonjdk.eclipse;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import org.eclipse.osgi.internal.loader.classpath.ClasspathEntry
import org.eclipse.osgi.internal.loader.classpath.ClasspathManager;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class EclipseInitAddFilesToClassLoaderGroovy extends AddFilesToClassLoaderGroovy{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public EclipseLunaClasspathAdd classpathManager;

    EclipseInitAddFilesToClassLoaderGroovy() {
    }


    @Override
    public void addFileImpl(File arg0) throws Exception {
        ClasspathEntry classpathEntry = EclipseLunaClasspathAdd.classpathManager.getExternalClassPath(arg0.getAbsolutePath(),
                EclipseLunaClasspathAdd.classpathManager.getGeneration());
        EclipseLunaClasspathAdd.al.add(classpathEntry);
        EclipseLunaClasspathAdd.propaget();

    }


}
