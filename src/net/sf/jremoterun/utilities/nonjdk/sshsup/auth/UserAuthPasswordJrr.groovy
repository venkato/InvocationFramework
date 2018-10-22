package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.Session
import com.jcraft.jsch.UserAuth
import com.jcraft.jsch.UserAuthPassword
import com.jcraft.jsch.UserAuthPasswordOriginal
import com.jcraft.jsch.UserAuthPasswordWithLogging
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession;

import java.util.logging.Logger;

@CompileStatic
class UserAuthPasswordJrr extends UserAuthPasswordWithLogging  implements SshAuthCallParentMethod{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    boolean start(Session session) throws Exception {
        if (session instanceof JrrJschSession) {
            JrrJschSession session2 = (JrrJschSession) session;
            schSessionLog = session2.jrrSchSessionLog
            if(org.apache.commons.lang.StringUtils.isEmpty(session2.getUserName())){
                log.warn "username not set"
            }
            if(org.apache.commons.lang.StringUtils.isEmpty(session2.getPassword2())){
                log.warn "password not set"
            }

        }

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
