package net.sf.jremoterun.utilities.nonjdk.sshsup.channels

import com.jcraft.jsch.ChannelShellOriginal
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.JrrJschSessionOriginal
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschIO
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession;

import java.util.logging.Logger;

@CompileStatic
class JrrChannelShell extends ChannelShellOriginal implements JrrJschSessionMethods {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrJschIO jrrJschIO = JrrJschIO.createJrrJschIOAndSet(this)
    volatile Runnable exitListener;
//    JrrJschSession jrrJschSession;

    JrrChannelShell() {
        super()
    }


    @Override
    void setExitStatus(int status) {
        super.setExitStatus(status)
        log.info "exit status ${status}"
        if (exitListener != null) {
            exitListener.run()
        }
    }




}
