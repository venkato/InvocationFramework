package net.sf.jremoterun.utilities.nonjdk.classpath.sl;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.LoadScriptFromFileUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory

import java.security.MessageDigest
import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
 */
@CompileStatic
class GroovySettingsLoader2 extends GroovySettingsLoader{
    private static final Logger log = Logger.getLogger(JrrClassUtils.currentClass.name);


    GroovySettingsLoader2() {
        this(JrrClassUtils.currentClassLoader)
    }

    GroovySettingsLoader2(ClassLoader parentClassLoaderToLoad) {
        super(parentClassLoaderToLoad)

    }

    Object loadSettings2(String scriptSource, String scriptName, Object param) {
        Class scriptClass = createScriptClass(scriptSource, scriptName)
        Object instance = scriptClass.newInstance()
        return LoadScriptFromFileUtils.runWithParams(instance, param, null)
    }


    Object loadSettings(File file, Object param) {
        try {
            return loadSettings2(file.text, file.name, param)
        } catch (Throwable e) {
            log.info("failed load ${file}",e)
            throw e
        }
    }
}
