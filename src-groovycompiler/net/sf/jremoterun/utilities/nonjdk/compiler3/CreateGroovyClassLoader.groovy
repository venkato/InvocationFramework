package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy

import java.util.logging.Logger

@CompileStatic
class CreateGroovyClassLoader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static URLClassLoader createGroovyClassLoader(ClassLoader parent) {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], parent)
        AddFilesToUrlClassLoaderGroovy adder1 = new AddFilesToUrlClassLoaderGroovy(urlClassLoader)
        adder1.addFileWhereClassLocated(GroovyObject)
        return createGroovyClassLoader2(urlClassLoader)
    }

    static URLClassLoader createGroovyClassLoader2(ClassLoader urlClassLoader) {
        URLClassLoader urlClassLoader2 = urlClassLoader.loadClass(GroovyClassLoader.name).newInstance(urlClassLoader) as URLClassLoader;
        assert urlClassLoader2.class.name == GroovyClassLoader.name
        assert !urlClassLoader2.class.is(GroovyClassLoader)
        return urlClassLoader2

    }


    static URLClassLoader findExtClassLoader() {
        ClassLoader loader = JrrClassUtils.getCurrentClassLoader()
        URLClassLoader classLoader = findExtClassLoaderImpl(loader)
        if (classLoader == null) {
            throw new Exception("failed find ext classloader for ${loader}")
        }
        return classLoader
    }

    static URLClassLoader findExtClassLoaderImpl(ClassLoader loader) {
        if (loader == null) {
            return null
        }
        if (loader.class.name == 'sun.misc.Launcher$ExtClassLoader') {
            return loader as URLClassLoader
        }
        return findExtClassLoaderImpl(loader.parent);
    }


}
