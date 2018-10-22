package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum MavenResolverMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2 {

    api,
    connector_basic,
    impl,
    named_locks,
    spi,
    transport_http,
    transport_wagon,
    util,
    ;


    MavenId m;

    MavenResolverMavenIds() {
        String artifactId = 'maven-resolver-' + name()
        artifactId = artifactId.replace('_', '-')
        m = new MavenId('org.apache.maven.resolver', artifactId, '1.7.0');
    }

    public static List<MavenResolverMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }


}
