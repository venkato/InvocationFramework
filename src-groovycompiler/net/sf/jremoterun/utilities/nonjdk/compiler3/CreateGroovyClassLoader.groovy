package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class CreateGroovyClassLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static ClRef groovyClassloaderClRef = new ClRef('groovy.lang.GroovyClassLoader')
    private static ClRef sunClassLoader = new ClRef('sun.misc.Launcher$ExtClassLoader')
    private static ClRef jdk11InternalClassLoaderApp = new ClRef('jdk.internal.loader.ClassLoaders$AppClassLoader')
    private static ClRef jdk11InternalClassLoader = new ClRef('jdk.internal.loader.ClassLoaders$PlatformClassLoader')


    static URLClassLoader createGroovyClassLoader(ClassLoader parent) {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], parent)
        AddFilesToUrlClassLoaderGroovy adder1 = new AddFilesToUrlClassLoaderGroovy(urlClassLoader)
        adder1.addFileWhereClassLocated(GroovyObject)
        return createGroovyClassLoader2(urlClassLoader)
    }

    static URLClassLoader createGroovyClassLoader2(ClassLoader urlClassLoader) {
        URLClassLoader urlClassLoader2 = urlClassLoader.loadClass(groovyClassloaderClRef.className).newInstance(urlClassLoader) as URLClassLoader;
        assert urlClassLoader2.getClass().getName() == groovyClassloaderClRef.className
        assert !urlClassLoader2.getClass().is(GroovyClassLoader)
        return urlClassLoader2

    }


    static ClassLoader findExtClassLoader() {
        ClassLoader loader = JrrClassUtils.getCurrentClassLoader()
        ClassLoader classLoader = findExtClassLoaderImpl(loader)
        if (classLoader == null) {
            throw new Exception("failed find ext classloader for ${loader}")
        }
        return classLoader
    }

    static ClassLoader findExtClassLoaderImpl(ClassLoader loader) {
        if (loader == null) {
            return null
        }
        if (loader.getClass().getName() == sunClassLoader.className) {
            return loader
        }
        if (loader.getClass().getName() == jdk11InternalClassLoader.className) {
//            ClassLoader classLoaderParent =  loader.getParent()
//            log.info "classLoaderParent = ${classLoaderParent}"
//            return classLoaderParent
            return loader;
        }
        return findExtClassLoaderImpl(loader.getParent());
    }


}
