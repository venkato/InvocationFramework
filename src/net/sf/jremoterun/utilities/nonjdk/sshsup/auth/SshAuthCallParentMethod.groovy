package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import com.jcraft.jsch.Session
import com.jcraft.jsch.UserAuth
import groovy.transform.CompileStatic

@CompileStatic
interface SshAuthCallParentMethod {

    boolean startCallSuper(Session session)

    UserAuth getUserAuth();

}
