package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.Session
import com.jcraft.jsch.UserAuth
import com.jcraft.jsch.UserAuthNoneOriginal
import com.jcraft.jsch.UserAuthNoneWithLogging
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession;

import java.util.logging.Logger;

@CompileStatic
class UserAuthNoneJrr extends UserAuthNoneWithLogging  implements SshAuthCallParentMethod{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean start(Session session) throws Exception {
        if (session instanceof JrrJschSession) {
            JrrJschSession session2 = (JrrJschSession) session;
            schSessionLog = session2.jrrSchSessionLog
        }
        boolean res = SshAuthHandlerJrr.authHandlerJrr.handle(this,session);
        schSessionLog.logMsg("allowed auth methods : ${getMethods()}")
        log.info ("allowed auth methods : ${getMethods()}")
        return res
    }

    @Override
    boolean   startCallSuper(Session session) {
        return super.start(session)
    }


    @Override
    UserAuth getUserAuth() {
        return this
    }
}
