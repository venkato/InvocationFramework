package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserAuth
import com.jcraft.jsch.UserAuthPublicKeyOriginal
import com.jcraft.jsch.UserAuthPublicKeyWithLogging
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession;

import java.util.logging.Logger;

@CompileStatic
class UserAuthPublicKeyJrr extends UserAuthPublicKeyWithLogging implements SshAuthCallParentMethod {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean start(Session session) throws Exception {
        if (session instanceof JrrJschSession) {
            JrrJschSession session2 = (JrrJschSession) session;
            schSessionLog = session2.jrrSchSessionLog
            IdentityRepository repository = session2.getIdentityRepository()
            if (repository == null) {
                log.warn "IdentityRepository is null"
            } else {
                Vector identities = repository.getIdentities()
                if (identities == null || identities.size() == 0) {
                    log.warn "Identities is empty or null"
                }
            }
            if (org.apache.commons.lang.StringUtils.isEmpty(session2.getUserName())) {
                log.warn "username not set"
            }
        }

        return SshAuthHandlerJrr.authHandlerJrr.handle(this, session);
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
