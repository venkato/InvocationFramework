package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.UserInfo
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef


import java.util.logging.Logger;

@CompileStatic
class UserInfoJrr implements UserInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static {
        new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.JcraftConnectopnOpener')
    }

    public UserInfo original;
    public JrrJschSession session;

    UserInfoJrr(UserInfo original, JrrJschSession session) {
        this.original = original
        this.session = session
    }

    @Override
    String getPassphrase() {
        return original.getPassphrase()
    }

    @Override
    String getPassword() {
        return original.getPassword();
    }

    @Override
    boolean promptPassword(String message) {
        log.info message
        return original.promptPassword(message)
    }

    @Override
    boolean promptPassphrase(String message) {
        log.info message
        return original.promptPassphrase(message)
    }

    @Override
    boolean promptYesNo(String message) {
        log.info message
        return original.promptYesNo(message)
    }

    @Override
    void showMessage(String message) {
        log.info message
        original.showMessage(message)
    }
}
