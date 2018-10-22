package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Pi4j implements MavenIdContains, ToFileRef2, EnumNameProvider  {
    pi4j_example,
    pi4j_device,
    pi4j_gpio_extension,
    pi4j_core,
    ;


    MavenId m;

    Pi4j() {
        m = new MavenId('com.pi4j', name().replace('_','-'), '1.3');
    }

    public static List<Pi4j> all = values().toList()

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    @Override
    String getCustomName() {
        return m.artifactId;
    }

}
