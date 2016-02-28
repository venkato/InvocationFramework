package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

@CompileStatic
enum NexusSearchMavenIds implements MavenIdContains {

    indexer('org.sonatype.nexus.plugins:nexus-indexer-lucene-model:2.14.9-01'),
    restletBridge('org.sonatype.nexus.plugins:nexus-restlet-bridge:2.14.9-01'),
    rest('org.sonatype.nexus:nexus-rest:3.12.1-01'),
    restlet1x('org.sonatype.nexus.plugins:nexus-restlet1x-model:2.14.9-01'),
    xstream('com.thoughtworks.xstream:xstream:1.4.10'),
    xpp3XmlParser('xpp3:xpp3:1.1.4c'),

    ;


    MavenId m;

    NexusSearchMavenIds(String mavenId) {
        m = new MavenId(mavenId);
    }


    public static List<NexusSearchMavenIds> all = (List) values().toList()


}
