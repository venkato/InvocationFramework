package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.URLClassLoaderExt
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class ClasspathTester {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void runTester(List filesAndMavenIds, Class tester, boolean checkNoGroovy) {
        URLClassLoaderExt classLoader = new URLClassLoaderExt(new URL[0], (ClassLoader) null)
        AddFilesToUrlClassLoaderGroovy adder = new AddFilesToUrlClassLoaderGroovy(classLoader)
        filesAndMavenIds.each { adder.addGenericEntery(it) }
        ContextClassLoaderWrapper.wrap2(classLoader,{
            if (checkNoGroovy) {
                ClassPathTesterHelper2.createClassPathTesterHelper2().checkNoSuchClass5(new ClRef(GroovyObject), classLoader)
                classLoader.addURL(UrlCLassLoaderUtils.getClassLocation(GroovyObject).toURL())
            } else {

            }
            Class<?> clazz = classLoader.loadClass(tester.name)
            Runnable runnable = clazz.newInstance() as Runnable
            runnable.run()

        })
    }

}
