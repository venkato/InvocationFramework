package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

import java.util.logging.Logger

@CompileStatic
enum Log4j2MavenIds implements MavenIdContains , EnumNameProvider, ToFileRef2{


    api,
    core,
    jcl,
    jul,
    slf4j_impl,
    ;

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    MavenId m;

    Log4j2MavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId("org.apache.logging.log4j:log4j-${artifactId}:2.14.1");
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
