package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum GroovyMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2 {
    // groovy_all, groovy_binary,groovy,groovy_backports_compat23,groovy_all_minimal,

    console,
    swing,
    nio,
    jsr223,
    sql,
    docgenerator,
    servlet,
    test,
    xml,
    json,
    groovysh,//
    groovydoc,
    templates,
    ant,
    bsf,
    jmx,
    testng,
    // macro,
//    tests_vm8,
    // performance,
//    eclipse_compiler, eclipse_batch, all_tests, xmlrpc, all_jdk14, jdk14,  http_builder,
    ;

    public static final MavenId groovyAll = new MavenId("org.codehaus.groovy", 'groovy-all', DropshipClasspath.groovy.m.version);

//    ;

    MavenId m;

    //'org.codehaus.groovy:groovy_console:2.4.13'
    GroovyMavenIds() {
//        String artifactId = name()
        String groovyVersion = DropshipClasspath.groovy.m.version
        m = new MavenId("org.codehaus.groovy", 'groovy-' + name(), groovyVersion);
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
