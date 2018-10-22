package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum MigLayoutMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2{

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
        m = new MavenId("com.miglayout", 'miglayout-' + name(), '5.2');
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
