package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.JrrKnowHostOriginal
import com.jcraft.jsch.KnownHosts
import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrJSch extends JSch{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrJSch() {
        JrrClassUtils.setFieldValue(this,'known_hosts',new JrrJrrKnowHost(this))
    }

    @Override
    Session getSession(String username, String host, int port) throws JSchException {
        assert host!=null
        JrrJschSession s = new JrrJschSession(this, username, host, port);
        return s
    }

    @Override
    protected void addSession(Session session) {
        log.info "connection established : ${session.getHost()} ${session.getPort()}"
        JrrJschSession s = session as JrrJschSession
        s.onConnected();
        super.addSession(session)
    }



}
