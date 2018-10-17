package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains;

import java.util.logging.Logger;

@CompileStatic
enum SshdMavenIds implements MavenIdContains {

    cli,
    spring_sftp,
    contrib,
    git,
    ldap,
    sftp,
    scp,
    netty,
    mina,
    core,
    putty,
    common,
//    apache_sshd,
//    sshd,
    ;


    MavenId m;

    SshdMavenIds() {
        String artifact = 'sshd-' + name().replace('_','-')
        m = new MavenId("org.apache.sshd", artifact, '2.1.0');
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()

}
