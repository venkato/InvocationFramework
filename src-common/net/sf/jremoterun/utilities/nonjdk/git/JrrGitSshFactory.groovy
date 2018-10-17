package net.sf.jremoterun.utilities.nonjdk.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.errors.TransportException
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.RemoteSession
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.util.FS;

import java.util.logging.Logger;

@CompileStatic
class JrrGitSshFactory extends JschConfigSessionFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean inited = false

    static void init() {
        if(inited){

        }else {
            inited = true
            JrrClassUtils.setFieldValue(SshSessionFactory, 'INSTANCE', new JrrGitSshFactory())
            JSch.setConfig("StrictHostKeyChecking", "no");
        }
    }

    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {

    }

    @Override
    RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
        return super.getSession(uri, credentialsProvider, fs, tms)
    }

    @Override
    protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
        return super.getJSch(hc, fs)
    }


}
