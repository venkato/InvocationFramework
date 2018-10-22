package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided;

import java.util.logging.Logger;

@CompileStatic
class UrlRef  implements ToFileRef2, UrlProvided{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public URL url;

    UrlRef(URL url) {
        this.url = url
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(url)
    }

    @Override
    URL convertToUrl() {
        return url
    }
}
