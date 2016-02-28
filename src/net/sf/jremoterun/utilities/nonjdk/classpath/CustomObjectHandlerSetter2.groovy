package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger

@CompileStatic
class CustomObjectHandlerSetter2 extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    Object get(Object key) {
        File f = key as File
        addSupport(f)
        return null
    }

    static void addSupport(File gitBase) {
        if (MavenDefaultSettings.mavenDefaultSettings.customObjectHandler == null) {

            CustomObjectHandlerImpl s = new CustomObjectHandlerImpl(gitBase)
            MavenDefaultSettings.mavenDefaultSettings.customObjectHandler = s
        } else {
            log.info "customObjectHandler already set : ${MavenDefaultSettings.mavenDefaultSettings.customObjectHandler}"
        }
    }

}
