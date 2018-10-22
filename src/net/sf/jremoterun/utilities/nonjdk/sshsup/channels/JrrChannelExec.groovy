package net.sf.jremoterun.utilities.nonjdk.sshsup.channels

import com.jcraft.jsch.ChannelExecOriginal
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschIO
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession
import net.sf.jremoterun.utilities.nonjdk.sshsup.SshCommandExec

import java.util.logging.Logger;

@CompileStatic
class JrrChannelExec extends ChannelExecOriginal implements JrrJschSessionMethods{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrJschIO jrrJschIO = JrrJschIO.createJrrJschIOAndSet(this);

    volatile Runnable exitListener;
//    JrrJschSession jrrJschSession;


    JrrChannelExec() {
        super()
    }

    @Override
    void setExitStatus(int status) {
        super.setExitStatus(status)
        log.info "exit status ${status}"
        if(exitListener!=null){
            exitListener.run()
        }
    }

    SshCommandExec prepareCommand(String cmd){
        SshCommandExec commandExec = new SshCommandExec(this,cmd)
        return commandExec;
    }

    @Override
    void setCommand(byte[] command1) {
        String cmd1=new String(command1)
        log.info "command = ${command1}"
        super.setCommand(command1)
    }

    @Override
    void setCommand(String command1) {
        log.info "command = ${command1}"
        super.setCommand(command1)
    }
}
