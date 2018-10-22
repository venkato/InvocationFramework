package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider


@CompileStatic
enum ProguardMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2{

    base,
    gradle,
    anttask,
    retrace,
    gui,
    ;
    
    MavenId m;

    ProguardMavenIds() {
        String artifactId = name()
        m = new MavenId("net.sf.proguard:proguard-${artifactId}:6.2.2");
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}