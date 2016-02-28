package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

import java.util.logging.Logger

@CompileStatic
enum Log4j2MavenIds implements MavenIdContains {


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
        m = new MavenId("org.apache.logging.log4j:log4j-${artifactId}:2.10.0");
    }

    public static List<? extends MavenIdContains> all = (List) values().toList()


}
