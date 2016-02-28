package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import com.sshtools.net.SocketTransport
import com.sshtools.publickey.SshPrivateKeyFile
import com.sshtools.publickey.SshPrivateKeyFileFactory
import com.sshtools.sftp.SftpClient
import com.sshtools.ssh.PasswordAuthentication
import com.sshtools.ssh.PublicKeyAuthentication
import com.sshtools.ssh.SshAuthentication
import com.sshtools.ssh.SshConnector
import com.sshtools.ssh.components.SshKeyPair
import com.sshtools.ssh2.Ssh2Client
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
abstract class SshConSet3 extends SshConSet2 implements Closeable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public SftpUtils sftpUtils

    void close() {
//        sftpUtils.close()
        if (sftpUtils != null) {
            sftpUtils.close()
        }
    }

    abstract Session getJcraftSession();

    abstract Object createJcraftConnection();


    void checkConnectAndCreateIfNeeded() {
        if (sftpUtils != null) {
            if (sftpUtils.ssh3 != null) {
                if (!sftpUtils.ssh3.isConnected()) {
                    log.info "not connected , reconnected to ${host} ${user}"
                    sftpUtils.ssh3 = null
                    sftpUtils.sftp2 = null
                    createNewSftpConn(sftpUtils)
                }
            }
        }
    }


    void createJcrftSftp(SftpUtils sftpUtils2) {
//        sftpUtils.sshConSet = this
        createJcraftConnection()
        Session session = getJcraftSession();
        assert session != null
        sftpUtils2.sftp = session.openChannel("sftp") as ChannelSftp
        sftpUtils2.sftp.connect()

    }

    void createNewSftpConn(SftpUtils sftpUtils2) {
        SocketTransport t = new SocketTransport(host, port);
        t.setTcpNoDelay(true);
        SshConnector con = SshConnector.createInstance();
        sftpUtils2.ssh3 = con.connect(t, user, true) as Ssh2Client;
        if (sshKey == null) {
            PasswordAuthentication pwd = new PasswordAuthentication();
            pwd.setPassword(password)
            assert sftpUtils2.ssh3.authenticate(pwd) == SshAuthentication.COMPLETE;
        } else {
            SshPrivateKeyFile keyFile = SshPrivateKeyFileFactory.parse(sshKey.bytes)
            SshKeyPair pair = keyFile.toKeyPair('')
            PublicKeyAuthentication pk = new PublicKeyAuthentication();
            pk.setPrivateKey(pair.getPrivateKey());
            pk.setPublicKey(pair.getPublicKey());
            assert sftpUtils2.ssh3.authenticate(pk) == SshAuthentication.COMPLETE;
        }
        SftpClient sftp2 = createSshtoolsSftpClient(sftpUtils2);
        sftpUtils2.sftp2 = sftp2
    }

    SftpClient  createSshtoolsSftpClient(SftpUtils sftpUtils2){
        SftpClient sftp2 = new SftpClient(sftpUtils2.ssh3);
        return sftp2
    }

    SftpUtils createSftpUtils() {
        if (sftpUtils != null) {
            return sftpUtils
        }

        SftpUtils sftpUtils2 = new SftpUtils()
//        sftpUtils2.sshConSet = this
        createJcrftSftp(sftpUtils2)
        createNewSftpConn(sftpUtils2)

        this.sftpUtils = sftpUtils2
        return sftpUtils2;
    }


}
