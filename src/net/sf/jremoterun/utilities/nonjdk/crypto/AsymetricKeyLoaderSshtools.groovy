package net.sf.jremoterun.utilities.nonjdk.crypto

import com.sshtools.publickey.SshPrivateKeyFile
import com.sshtools.publickey.SshPrivateKeyFileFactory
import com.sshtools.publickey.SshPublicKeyFile
import com.sshtools.publickey.SshPublicKeyFileFactory
import com.sshtools.ssh.components.SshKeyPair
import com.sshtools.ssh.components.SshPrivateKey
import com.sshtools.ssh.components.SshPublicKey
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.PrivateKey
import java.security.PublicKey;
import java.util.logging.Logger;


// see also com.trilead.ssh2.crypto.PEMDecoder.decode
@CompileStatic
class AsymetricKeyLoaderSshtools {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static PrivateKey loadPrivateKeyUseful(byte[] f, String passprase){
        SshPrivateKey key = loadPrivateKey2(f, passprase)
        return JrrClassUtils.getFieldValue(key,'prv') as PrivateKey
    }

    static SshPrivateKey loadPrivateKey2(byte[] f,String passprase){
        SshKeyPair sshKeyPair = loadPrivateKey(f,passprase)
        SshPrivateKey privateKey = sshKeyPair.getPrivateKey()
        return privateKey;
    }

    static SshKeyPair loadPrivateKey(byte[] f,String passprase){
        SshPrivateKeyFile parse = SshPrivateKeyFileFactory.parse(f)
        SshKeyPair sshKeyPair = parse.toKeyPair(passprase);
        return sshKeyPair
    }

    static PublicKey loadPublicKeyUseful(byte[] f){
        // TODO use instance of private key
        SshPublicKey key = loadPublicKey(f)
        try {
            PublicKey publicKey = JrrClassUtils.getFieldValue(key, 'pubKey') as PublicKey;
            return publicKey
        }catch(NoSuchFieldException e){
            log.info("seems field not exit pubKey",e)
        }
        try {
            PublicKey publicKey = JrrClassUtils.getFieldValue(key, 'pubkey') as PublicKey;
            return publicKey
        }catch(NoSuchFieldException e){
            log.info("seems field not exit pubkey",e)
        }
        PublicKey publicKey = JrrClassUtils.getFieldValue(key, 'pub') as PublicKey;
        return publicKey
    }

    static SshPublicKey loadPublicKey(byte[] bytes){
        SshPublicKeyFile sshPublicKeyFile = SshPublicKeyFileFactory.parse(bytes)
        SshPublicKey publicKey = sshPublicKeyFile.toPublicKey()
        return publicKey;
    }



}
