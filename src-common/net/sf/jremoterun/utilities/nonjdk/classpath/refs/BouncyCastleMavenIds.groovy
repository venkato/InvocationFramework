package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

@CompileStatic
enum BouncyCastleMavenIds implements MavenIdContains {


    bcprov_ext_debug_jdk15on,
//    bcprov_ext_jdk15on,

    bcmail_jdk15on,
    bcpg_jdk15on,
    bctls_jdk15on,
    bcpkix_jdk15on,
    bcprov_jdk15on,
    ;
    // war


    MavenId m;

    BouncyCastleMavenIds() {
        m = new MavenId('org.bouncycastle', name().replace('_', '-'), '1.60');
    }

    public static List<BouncyCastleMavenIds> all = values().toList()

}
