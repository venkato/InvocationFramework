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

    SshConSet2 conSet2

    Session session

    JSch jsch;

    void init(){
        JSch jsch2 = new JSch();
        configure(jsch2);
    }

    void configure(JSch jsch){
        this.jsch = jsch
        defaultAddKnownHosts()
        defaultPrivateKey()
        session = createSession();
        if(conSet2.password!=null){
            session.password = conSet2.password
            session.setUserInfo(this)
        }
        final java.util.Properties config = new java.util.Properties();
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        configureSession(session, config);
        session.connect();
        session.setTimeout(0);
    }

    void configureSession(Session session, Properties config) {
        session.setConfig(config);
        session.setTimeout(5000);
    }

    Session createSession(){
        Session session2 = jsch.getSession(conSet2.user, conSet2.host, conSet2.port);
        return session2
    }

    void defaultAddKnownHosts(){
        if (conSet2.knownHosts!=null) {
            jsch.setKnownHosts(conSet2.knownHosts.absolutePath);
        }
    }

    void defaultPrivateKey(){
        if (conSet2.sshKey!=null) {
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
        log.info "${message}"
    }
}
