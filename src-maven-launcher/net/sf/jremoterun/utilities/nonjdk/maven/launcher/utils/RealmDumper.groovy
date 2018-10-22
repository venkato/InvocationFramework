package net.sf.jremoterun.utilities.nonjdk.maven.launcher.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenDownloadHttpHandler
import org.codehaus.plexus.classworlds.ClassWorld
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.util.logging.Logger;

@CompileStatic
class RealmDumper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void dumpClassloader(org.codehaus.plexus.classworlds.realm.ClassRealm realm) {
        URL[] urLs = realm.getURLs();
        log.info("urls : " + Arrays.toString(urLs));
//        Thread.dumpStack();
//        ClassRealm parentRealm = realm.getParentRealm();
        ClassWorld world = realm.getWorld();
        Collection<ClassRealm> realms = world.getRealms();
        int count=1;
        for (ClassRealm realm23 : realms) {
            "" + realm23.getId() + " " + realm23 + " " + Arrays.toString(realm23.getURLs())
            log.info("count = ${count} : ${realm23.getId()} ${realm23} ${realm23.getURLs()}");
            count++
        }
    }


}
