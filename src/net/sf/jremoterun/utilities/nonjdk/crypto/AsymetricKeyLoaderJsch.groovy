package net.sf.jremoterun.utilities.nonjdk.crypto

import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class AsymetricKeyLoaderJsch {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static KeyPair load(JSch jSch, File privateKeyF, File publicKeyF) {
        if (jSch == null) {
            jSch = new JSch();
        }
        byte[] privateKeyB;
        byte[] publicKeyB;
        if (privateKeyF != null) {
            privateKeyB = privateKeyF.bytes
        }
        if (publicKeyF != null) {
            publicKeyB = publicKeyF.bytes
        }
        KeyPair keyPair = KeyPair.load(jSch, privateKeyB, publicKeyB)
        return keyPair;
    }


}
