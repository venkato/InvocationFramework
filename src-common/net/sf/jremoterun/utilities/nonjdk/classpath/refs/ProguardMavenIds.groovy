package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains


@CompileStatic
enum ProguardMavenIds implements MavenIdContains{

    base,
    gradle,
    anttask,
    retrace,
    gui,
    ;
    
    MavenId m;

    ProguardMavenIds() {
        String artifactId = name()
        m = new MavenId("net.sf.proguard:proguard-${artifactId}:5.3.3");
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()
}