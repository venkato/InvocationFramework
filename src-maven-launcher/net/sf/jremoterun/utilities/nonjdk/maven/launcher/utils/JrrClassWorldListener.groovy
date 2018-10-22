package net.sf.jremoterun.utilities.nonjdk.maven.launcher.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.plexus.classworlds.ClassWorldListener
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.util.logging.Logger;

@CompileStatic
class JrrClassWorldListener implements ClassWorldListener {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void realmCreated(ClassRealm realm) {
        log.info "realm created ${realm.getId()} ${realm.getURLs()}"
    }

    @Override
    void realmDisposed(ClassRealm realm) {
        log.info "realm disposed ${realm.getId()} ${realm.getURLs()}"
    }
}
