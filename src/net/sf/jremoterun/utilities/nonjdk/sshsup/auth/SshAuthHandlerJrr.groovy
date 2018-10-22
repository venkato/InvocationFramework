package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession
import com.jcraft.jsch.JrrSchSessionLog;

import java.util.logging.Logger;

@CompileStatic
class SshAuthHandlerJrr {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile SshAuthHandlerJrr authHandlerJrr = new SshAuthHandlerJrr();

    boolean handle(SshAuthCallParentMethod handle, Session session) {
        AuthState authState = new AuthState(handle);
        Class cl = handle.getClass();
        JrrSchSessionLog sessionLog;
        long startTime = System.currentTimeMillis()
        try {

            if (session instanceof JrrJschSession) {
                JrrJschSession jrrSession = (JrrJschSession) session;
                jrrSession.auths.add(authState)
                sessionLog = jrrSession.jrrSchSessionLog
                sessionLog.logMsg "doing ${cl.getSimpleName()}"
            }else{
                log.info "auth log not saved due to session not JrrJschSession : ${session.getClass().getName()}"
            }
            boolean b = handle.startCallSuper(session)
            if (b) {
                authState.state = AuthStateEnum.finishedOk;
            } else {
                authState.state = AuthStateEnum.finishedFailed;
            }
            return b;
        } catch (Throwable e) {
            authState.exception = e;
            authState.state = AuthStateEnum.finishedFailed;
            if(sessionLog!=null) {
                sessionLog.logMsg "connecting failed ${e}"
            }
            throw e;
        }finally{
            long duration = System.currentTimeMillis() - startTime;
            duration = (long)(duration/1000);
            log.info "${cl.getSimpleName()} ${session.getHost()} with state ? ${authState.state} within ${duration}s"
            if(sessionLog!=null) {
                sessionLog.logMsg "${cl.getSimpleName()} finished with state ? ${authState.state} within ${duration}s"
            }
        }
    }

}
