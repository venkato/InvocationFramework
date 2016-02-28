package net.sf.jremoterun.utilities.nonjdk.sfdownloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class SfLink implements Serializable, ToFileRef2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    https://netix.dl.sourceforge.net/project/pyscripter/PyScripter-v3.3/PyScripter-v3.3.2-Setup.exe

    String path;

    SfLink(String path) {
        this.path = path
    }

    @Override
    String toString() {
        return path
    }


    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if (handler == null) {
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)
    }


}
