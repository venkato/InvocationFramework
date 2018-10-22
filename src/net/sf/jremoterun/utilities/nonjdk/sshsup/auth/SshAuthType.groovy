package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum SshAuthType implements EnumNameProvider{

    none( new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.auth.UserAuthNoneJrr')),
    password(new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.auth.UserAuthPasswordJrr')),
    keyboard_interactive(new ClRef( 'net.sf.jremoterun.utilities.nonjdk.sshsup.auth.UserAuthKeyboardInteractiveJrr')),
    publickey(new ClRef( 'net.sf.jremoterun.utilities.nonjdk.sshsup.auth.UserAuthPublicKeyJrr')),
    gssapi_with_mic( new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.auth.UserAuthGSSAPIWithMICJrr')),
    ;

    String customName;
    public ClRef className;

    SshAuthType(Class className) {

    }
    SshAuthType(ClRef className) {
        customName = name().replace('_','-');
        this.className = className
    }
}
