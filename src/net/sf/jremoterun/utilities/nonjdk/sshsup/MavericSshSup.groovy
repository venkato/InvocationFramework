package net.sf.jremoterun.utilities.nonjdk.sshsup


import com.sshtools.ssh.components.ComponentManager
import com.sshtools.ssh.components.jce.JCEComponentManager;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MavericSshSup {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void addSupportedProtocolsForSsh(){
        JCEComponentManager instance = (JCEComponentManager)ComponentManager.getInstance()
        instance.installCBCCiphers( instance.supportedSsh2CiphersCS())
        instance.installCBCCiphers( instance.supportedSsh2CiphersSC())
    }

}
