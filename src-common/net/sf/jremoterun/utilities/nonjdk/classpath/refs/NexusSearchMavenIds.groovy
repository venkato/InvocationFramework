package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
enum NexusSearchMavenIds implements MavenIdContains, ToFileRef2 {

    indexer('org.sonatype.nexus.plugins:nexus-indexer-lucene-model:2.14.20-01'),
    restletBridge('org.sonatype.nexus.plugins:nexus-restlet-bridge:2.14.20-01'),
    rest('org.sonatype.nexus:nexus-rest:3.30.0-01'),
    restlet1x('org.sonatype.nexus.plugins:nexus-restlet1x-model:2.14.20-01'),
    xstream('com.thoughtworks.xstream:xstream:1.4.16'),
    xpp3XmlParser('xpp3:xpp3:1.1.4c'),

    ;


    MavenId m;

    NexusSearchMavenIds(String mavenId) {
        m = new MavenId(mavenId);
    }


    public static List<NexusSearchMavenIds> all = (List) values().toList()

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}
