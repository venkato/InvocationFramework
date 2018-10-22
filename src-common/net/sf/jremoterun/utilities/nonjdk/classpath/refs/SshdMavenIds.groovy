package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum SshdMavenIds implements MavenIdContains , EnumNameProvider, ToFileRef2{

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
    //openpgp,
    putty,
    common,
//    apache_sshd,
//    sshd,
    ;


    MavenId m;

    SshdMavenIds() {
        String artifact = 'sshd-' + name().replace('_','-')
        // only 2.1.0 compatible with groovySsh : 2.2.0 not
        //new ClRef('me.bazhenov.groovysh.GroovyShellService');
        m = new MavenId("org.apache.sshd", artifact, '2.1.0');

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
