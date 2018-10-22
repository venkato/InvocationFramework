package net.sf.jremoterun.utilities.nonjdk.maven.mavenupload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenResolverMavenIds;

import java.util.logging.Logger;

@CompileStatic
class MavenUploaderMavenId {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<MavenIdContains> neededMavenIds = (List)[
            MavenMavenIds. artifact,
            MavenMavenIds.builder_support,
            MavenMavenIds.model_builder,
            MavenMavenIds.model,
            MavenMavenIds.repository_metadata,
            MavenMavenIds.resolver_provider,
            MavenMavenIds.slf4j_provider,
            LatestMavenIds.plexus_utils,
            MavenResolverMavenIds.api,
            MavenResolverMavenIds.connector_basic,
            MavenResolverMavenIds.impl,
            MavenResolverMavenIds.named_locks,
            MavenResolverMavenIds.spi,
            MavenResolverMavenIds.transport_http,
            MavenResolverMavenIds.util,

    ]



}
