package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum MavenMavenIdRandom implements MavenIdContains, EnumNameProvider, ToFileRef2 {

    sharedUtils('org.apache.maven.shared:maven-shared-utils:3.2.1'),
    wagonProviderApi('org.apache.maven.wagon:wagon-provider-api:3.3.3'),
    ;


    MavenId m;

    MavenMavenIdRandom(String m2) {
        this.m = new MavenId(m2)
    }

    MavenMavenIdRandom(MavenIdContains m) {
        this.m = m.getM()
    }

    public static List<MavenMavenIdRandom> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

}
