package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum MavenMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2 {

    artifact,
    builder_support,
    compat,
    core,
    embedder,
    model,
    model_builder,
    plugin_api,
    repository_metadata,
    resolver_provider,
    settings,
    settings_builder,
    slf4j_provider,
    ;


    MavenId m;

    MavenMavenIds() {
        String artifactId = 'maven-'+name().replace('_', '-')
        m = new MavenId('org.apache.maven', artifactId, '3.6.2');
    }

    public static List<MavenMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }


}
