package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided;

import java.util.logging.Logger;

@CompileStatic
enum CustomRefsUrls implements UrlProvided{

    pureJavacommnyJetBrainsUrl('https://jetbrains.bintray.com/pty4j/org/jetbrains/pty4j/purejavacomm/0.0.11.1/purejavacomm-0.0.11.1.jar'),
    ;


    String url;

    CustomRefsUrls(String url) {
        this.url = url
    }

    @Override
    URL convertToUrl() {
        return new URL(url)
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(url)
    }
}
