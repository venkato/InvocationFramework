package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum BouncyCastleMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2 {


    bcprov_ext_debug_jdk15on,
//    bcprov_ext_jdk15on,

    bcmail_jdk15on,
    bcpg_jdk15on,
//    bctls_jdk15on,
    bcpkix_jdk15on,
    bcprov_jdk15on,

    bctls_jdk15on,
    bctls_jdk15to18,
    bcprov_jdk15to18,
    bcutil_jdk15to18,
    ;
    // war


    MavenId m;

    BouncyCastleMavenIds() {
        m = new MavenId('org.bouncycastle', name().replace('_', '-'), '1.69');
    }

    public static List<BouncyCastleMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}
