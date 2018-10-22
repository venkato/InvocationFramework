package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum AntMavenIds implements MavenIdContains , EnumNameProvider{

    ant,
    ant_antlr,
    ant_junit4,
    ant_junit,
    ant_junitlauncher,
    ant_launcher,

    ;

    MavenId m;

    AntMavenIds() {
        String artifact =name().replace('_','-')
        m = new MavenId("org.apache.ant", artifact, '1.10.5');
    }



    public static List<? extends MavenIdContains> all = (List) values().toList()

    @Override
    String getCustomName() {
        return m.artifactId
    }
}
