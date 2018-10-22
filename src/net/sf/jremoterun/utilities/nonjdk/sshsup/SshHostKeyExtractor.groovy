package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.sshtools.net.SocketTransport
import com.sshtools.ssh.HostKeyVerification
import com.sshtools.ssh.SshClient
import com.sshtools.ssh.SshConnector
import com.sshtools.ssh.SshContext
import com.sshtools.ssh.SshException
import com.sshtools.ssh.components.SshPublicKey
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class SshHostKeyExtractor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static SshPublicKey extractHostKey(String host, int port, String user) {
        SshConnector con = SshConnector.createInstance();
        SshPublicKey keyR

        HostKeyVerification hostKeyVerification1 = new HostKeyVerification() {
            boolean verifyHost(String name, SshPublicKey key) throws SshException {
                keyR = key;
                return false;
            }
        };
        SshContext context = con.getContext();
        context.setHostKeyVerification(hostKeyVerification1);
        SocketTransport t = new SocketTransport(host, port);
        t.setTcpNoDelay(true);

        try {
            SshClient connect = con.connect(t, user, true);
            connect.disconnect();
        } catch (SshException e) {
            log.log(Level.FINE, "expected exception", e)
        }
        return keyR;
    }


}
