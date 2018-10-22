package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class JcraftConnectopnOpener implements UserInfo {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int defaultLogonTimeout = 10000;
    public int logonTimeoutInMs = defaultLogonTimeout;

    /**
     * -1 : mean disable
     */
    public static int defaultAfterLogonTimeout = -1

    SshConSet2 conSet2

    JrrJschSession session

    JSch jsch;

    boolean showMessageProp = true

    String lastMessage;

    void init() {
        JrrJSch jsch2 = new JrrJSch();
        configure(jsch2);
    }

    void configure(JSch jsch) {
        this.jsch = jsch
        defaultAddKnownHosts()
        defaultPrivateKey()
        session = createSession();
        if (conSet2.password != null) {
            session.setPassword( conSet2.password)
            session.setUserInfo(this)
        }
        if(conSet2.user!=null){
            if (session instanceof JrrJschSession) {
                JrrJschSession  session7= (JrrJschSession) session;
                session7.setUserName(conSet2.user)

            }

        }
        final java.util.Properties config = new java.util.Properties();
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        configureSession(session, config);
        session.connect();
        if (defaultAfterLogonTimeout >= 0) {
            session.setTimeout(defaultAfterLogonTimeout);
        }
    }

    void configureSession(Session session, Properties config) {
        session.setConfig(config);
        if(logonTimeoutInMs>=0) {
            session.setTimeout(logonTimeoutInMs);
        }
    }

    JrrJschSession createSession() {
        assert conSet2.host!=null
        //Session session2 = jsch.getSession(conSet2.user, conSet2.host, conSet2.port);
        JrrJschSession session2 = new JrrJschSession(jsch,conSet2.user, conSet2.host, conSet2.port);
        session2.jcraftConnectopnOpener = this
        return session2
    }

    void defaultAddKnownHosts() {
        if (conSet2.knownHosts != null) {
            jsch.setKnownHosts(conSet2.knownHosts.absolutePath);
        }
    }

    void defaultPrivateKey() {
        if (conSet2.sshKey != null) {
            jsch.addIdentity(conSet2.sshKey.absolutePath);
        }
    }

    @Override
    String getPassphrase() {
        return null
    }

    @Override
    String getPassword() {
        return conSet2.password
    }

    @Override
    boolean promptPassword(String message) {
        return false
    }

    @Override
    boolean promptPassphrase(String message) {
        return false
    }

    @Override
    boolean promptYesNo(String message) {
        return false
    }

    @Override
    void showMessage(String message) {
        if (conSet2.showMessage) {
            log.info "${message}"
        }
        lastMessage = message
    }
}
