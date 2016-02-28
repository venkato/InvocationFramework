package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum MigLayoutMavenIds implements MavenIdContains{

    examples,
    demo,
    ideutil,
    javafx,
    swt,
    swing,
    core;


    MavenId m;

    //'org.codehaus.groovy:groovy_console:2.4.13'
    MigLayoutMavenIds() {
//        String artifactId = name()
        m = new MavenId("com.miglayout", 'miglayout-' + name(), '5.1');
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()



}
