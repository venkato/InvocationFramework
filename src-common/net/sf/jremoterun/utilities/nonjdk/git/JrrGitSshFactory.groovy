package net.sf.jremoterun.utilities.nonjdk.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJSch
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession
import org.eclipse.jgit.errors.TransportException
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.JschSession
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.RemoteSession
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.util.FS;

import java.util.logging.Logger;

@CompileStatic
class JrrGitSshFactory extends JschConfigSessionFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean useJrrSsh = false
    public static volatile boolean inited = false
    public static volatile JrrGitSshFactory gitSshFactory;

    static void init() {
        if(inited){

        }else {
            inited = true
            gitSshFactory = new JrrGitSshFactory();
            JrrClassUtils.setFieldValue(SshSessionFactory, 'INSTANCE', gitSshFactory)
            JSch.setConfig("StrictHostKeyChecking", "no");
        }
    }

    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {

    }



//    @Override
//    RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
//        return super.getSession(uri, credentialsProvider, fs, tms)
//    }

    @Override
    protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
//        log.info "getting jsch .."
        return super.getJSch(hc, fs)
    }

    @Override
    protected Session createSession(OpenSshConfig.Host hc, String user, String host, int port, FS fs) throws JSchException {
        log.info "useJrrSsh = ${useJrrSsh}"
        if(useJrrSsh) {
            JSch jSch = getJSch(hc, fs);
            JrrJschSession s = new JrrJschSession(jSch, user, host, port);
            return s;
        }
        return super.createSession(hc,user,host,port,fs);
    }

    @Override
    RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
        RemoteSession session1 = super.getSession(uri, credentialsProvider, fs, tms)
//        if (session instanceof JschSession) {
//            JschSession sessionSsh = (JschSession) session;
//            return new RemoteSessionGitJrr(sessionSsh.disconnect())
//        }
        return session1
    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        log.info "getting jsch .."
        if(useJrrSsh) {
            final JrrJSch jsch = new JrrJSch();
            JSch.setConfig("ssh-rsa", JSch.getConfig("signature.rsa")); //$NON-NLS-1$ //$NON-NLS-2$
            JSch.setConfig("ssh-dss", JSch.getConfig("signature.dss")); //$NON-NLS-1$ //$NON-NLS-2$
            configureJSch(jsch);
            JrrClassUtils.invokeJavaMethod(JschConfigSessionFactory, 'knownHosts', jsch, fs);
            JrrClassUtils.invokeJavaMethod(JschConfigSessionFactory, 'identities', jsch, fs);
            return jsch;
        }else {
            return super.createDefaultJSch(fs)
        }
    }
}
