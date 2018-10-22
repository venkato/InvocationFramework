package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.JSch
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class InitAuth {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static volatile boolean inited = false

    static void init() {
        if (inited) {
            log.info "already inited"
        } else {
            inited = true
            initImpl()
        }
    }

    static void initImpl() {
        SshAuthType.values().toList().each {
            putAuth(it,it.className);
        }
//        putAuth(SshAuthType.none, UserAuthNoneJrr)
//        putAuth(SshAuthType.password", UserAuthPasswordJrr)
//        putAuth(SshAuthType.keyboard-interactive, UserAuthKeyboardInteractiveJrr)
//        putAuth(SshAuthType.publickey, UserAuthPublicKeyJrr)
//        putAuth(SshAuthType.gssapi_with_mic, UserAuthGSSAPIWithMICJrr)
    }

    static void putAuth(SshAuthType name, ClRef clazz) {
        JSch.setConfig('userauth.' + name.customName, clazz.getName())
    }

}
