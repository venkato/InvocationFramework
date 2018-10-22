package net.sf.jremoterun.utilities.nonjdk.git

import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.transport.JschSession
import org.eclipse.jgit.transport.RemoteSession
import org.eclipse.jgit.transport.URIish;

import java.util.logging.Logger;

@CompileStatic
class RemoteSessionGitJrr extends JschSession{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    RemoteSessionGitJrr(Session session, URIish uri) {
        super(session, uri)
    }
}
