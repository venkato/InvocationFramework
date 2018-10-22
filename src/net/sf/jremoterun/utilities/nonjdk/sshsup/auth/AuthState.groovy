package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class AuthState {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final SshAuthCallParentMethod handle;
    public AuthStateEnum state = AuthStateEnum.inProgress;
    public Throwable exception;

    AuthState(SshAuthCallParentMethod handle) {
        this.handle = handle
    }

    @Override
    String toString() {
        return "${handle.getClass().getSimpleName()} ${state} ${exception}"
    }
}
