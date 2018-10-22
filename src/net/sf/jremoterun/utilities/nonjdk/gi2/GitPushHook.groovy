package net.sf.jremoterun.utilities.nonjdk.gi2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import org.eclipse.jgit.transport.URIish

import java.util.logging.Logger

@CompileStatic
class GitPushHook extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static boolean enabledCheck = true;
    public static Collection<String> enabledUrlPushList = [];
    public static String enabledUrlPush = "";


    @Override
    public Object get(Object key) {
        if (key instanceof org.eclipse.jgit.transport.Transport) {
            org.eclipse.jgit.transport.Transport transport = (org.eclipse.jgit.transport.Transport) key;
            checkImpl(transport)
        }
        return null;
    }


    void checkImpl(org.eclipse.jgit.transport.Transport transport) {
        if (enabledCheck) {
            log.info "checking : ${transport.getURI()}"
            URIish uris = transport.getURI();
            String uriS = uris.toString().toLowerCase()
            String foundS = enabledUrlPushList.find { uriS.startsWith(it) }
            if(foundS!=null){
                if (!uris.toString().startsWith(enabledUrlPush)) {
                    throw new IllegalStateException("push not allowed for : " + uris);
                }
            }
        }else{
            log.info "check disabled, pushing ${transport.getURI()}"
        }

    }

}
