package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.Session
import com.jcraft.jsch.UserAuth
import com.jcraft.jsch.UserAuthGSSAPIWithMICOriginal
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class UserAuthGSSAPIWithMICJrr extends UserAuthGSSAPIWithMICOriginal implements SshAuthCallParentMethod{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean start(Session session) throws Exception {
        return SshAuthHandlerJrr.authHandlerJrr.handle(this,session);
    }

    @Override
    boolean startCallSuper(Session session) {
        return super.start(session)
    }

    @Override
    UserAuth getUserAuth() {
        return this
    }
}
