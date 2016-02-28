package net.sf.jremoterun.utilities.nonjdk.gi2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import org.eclipse.jgit.transport.URIish

import java.util.logging.Logger

@CompileStatic
class GitPushHook extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String enabledUrlPush = "";


    @Override
    public Object get(Object key) {
        if (key instanceof org.eclipse.jgit.transport.Transport) {
            org.eclipse.jgit.transport.Transport transport = (org.eclipse.jgit.transport.Transport) key;
            checkImpl(transport)
        }
        return null;
    }


    void checkImpl(org.eclipse.jgit.transport.Transport transport){
        log.info "checking : ${transport.getURI()}"
        URIish uris = transport.getURI();
        if (!uris.toString().startsWith(enabledUrlPush)) {
            throw new IllegalStateException("push not allowed for : " + uris);
        }

    }

}
