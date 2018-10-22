package net.sf.jremoterun.utilities.nonjdk.sshsup.channels

import com.jcraft.jsch.Session
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschIO
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession

@CompileStatic
interface JrrJschSessionMethods {

//    void setJrrJschSession(JrrJschSession jrrJschSession)

//    void setSession(Session session)

//    void onChannelClosedNotify();

    JrrJschIO getJrrJschIO();

    void setExitListener(Runnable r);



}
