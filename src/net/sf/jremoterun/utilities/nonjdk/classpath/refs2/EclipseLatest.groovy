package net.sf.jremoterun.utilities.nonjdk.classpath.refs2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.search.MavenResponseParser
import net.sf.jremoterun.utilities.nonjdk.classpath.search.MavenSearch;

import java.util.logging.Logger;

@CompileStatic
class EclipseLatest {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String eclipseGroupId = 'org.eclipse.platform'

    static List<MavenId> findLatestEclipseMavenIds() {
        MavenSearch mavenSearch = new MavenSearch();
        Map raw = mavenSearch.findMavenIdsAllArtifactsWithGroupIdRaw(eclipseGroupId, 500);
        List<MavenId> response2 = new MavenResponseParser().parseAllWithGroupLatestResponse2(raw);
        return response2;

    }

}
