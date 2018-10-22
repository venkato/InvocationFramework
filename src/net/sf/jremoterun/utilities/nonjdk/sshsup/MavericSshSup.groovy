package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.sshtools.net.SocketTransport
import com.sshtools.ssh.HostKeyVerification
import com.sshtools.ssh.SshClient
import com.sshtools.ssh.SshConnector
import com.sshtools.ssh.SshContext
import com.sshtools.ssh.SshException
import com.sshtools.ssh.components.ComponentManager
import com.sshtools.ssh.components.SshPublicKey
import com.sshtools.ssh.components.jce.JCEComponentManager;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MavericSshSup {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void addSupportedProtocolsForSsh() {
        JCEComponentManager instance = (JCEComponentManager) ComponentManager.getInstance()
        instance.installCBCCiphers(instance.supportedSsh2CiphersCS())
        instance.installCBCCiphers(instance.supportedSsh2CiphersSC())
    }



}
