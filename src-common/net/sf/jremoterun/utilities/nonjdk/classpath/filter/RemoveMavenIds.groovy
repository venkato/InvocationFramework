package net.sf.jremoterun.utilities.nonjdk.classpath.filter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.store.ListStore

import java.util.logging.Logger

@CompileStatic
class RemoveMavenIds extends ClassPathFilter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<MavenId> mavenIds

    Set<String> cache

    static List doFIlter3(List list, File fileWithMavenIds) {
        ListStore<MavenId> idListStore = new ListStore<>(fileWithMavenIds)
        List<MavenId> mavenIds = idListStore.loadsettings()
        return doFIlter3(list, mavenIds)
    }

    static List doFIlter3(List list, List<MavenId> mavenIds) {
        RemoveMavenIds removeMavenIds = new RemoveMavenIds()
        removeMavenIds.mavenIds = mavenIds;
        return removeMavenIds.doFilter(list)
    }

    void buildCache() {
        if (cache == null) {
            cache = new HashSet<>(mavenIds.collect { buildKey(it) })
        }
    }

    String buildKey(MavenId mavenId) {
        return mavenId.groupId + ':' + mavenId.artifactId
    }


    @Override
    Object onMavenId(MavenId m) {
        buildCache()
        String key = buildKey(m)
        if (cache.contains(key)) {
            return null
        }
        return super.onMavenId(m)
    }

}
