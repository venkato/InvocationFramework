package net.sf.jremoterun.utilities.nonjdk.crypto

import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.Key
import java.security.Provider
import java.security.Security;
import java.util.logging.Logger;

@CompileStatic
class AsymetricKeyLoaderAny {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean bcRegistered = false

    static Object loadKeyAny(File f, KeyType type, Boolean isPrivateKey) {
        if (isPrivateKey == null) {
            String name = f.getName()
            if (name == 'id_dsa') {
                isPrivateKey = true
                if (type == null) {
                    type = KeyType.DSA
                }
            }
            if (name == 'id_rsa') {
                isPrivateKey = true
                if (type == null) {
                    type = KeyType.RSA
                }
            }
            if (name.endsWith('.pub')) {
                isPrivateKey = false
            }
        }
        String type2;
        if (type != null) {
            type2 = type.name();
        }
        return loadKeyAny(f.bytes, type2, isPrivateKey)
    }

    static Object loadKeyAny(byte[] bytes, String type, Boolean isPrivateKey) {
        if (!bcRegistered) {
            bcRegistered = true;
            Provider provider = JavaSecurityProviders.BC.className.newInstance3() as Provider;
            Security.addProvider(provider);
        }

        if (isPrivateKey == null || !isPrivateKey) {
            try {
                return AsymetricKeyLoader3.loadX509Certificate(bytes)
            } catch (Throwable e) {
                log.warn("failed load x509 certificate", e)
            }
        }
        try {
            return AsymetricKeyLoader3.loadKey(bytes, type, null);
        } catch (Throwable e) {
            log.warn("failed load key using ${AsymetricKeyLoader3.getSimpleName()}", e)
        }

        if (isPrivateKey == null) {
            try {
                return AsymetricKeyLoaderSshtools.loadPrivateKeyUseful(bytes, null)
            } catch (Throwable e) {
                log.warn("failed load sshtool private", e)
            }
            try {
                return AsymetricKeyLoaderSshtools.loadPublicKeyUseful(bytes)
            } catch (Throwable e) {
                log.warn("failed load sshtool public", e)
            }

        } else {
            try {
                if (isPrivateKey) {
                    return AsymetricKeyLoaderSshtools.loadPrivateKeyUseful(bytes, null)
                } else {
                    return AsymetricKeyLoaderSshtools.loadPublicKeyUseful(bytes)
                }
            } catch (Throwable e) {
                log.warn("failed load sshtool common", e)
            }
        }
        try {
            return AsymetricKeyBC2.loadKeyBcUseful(bytes)
        } catch (Throwable e) {
            log.warn("failed load bc KeyPair ", e)
        }
        if (isPrivateKey == null) {
            try {
                JSch jSch = new JSch()
                return KeyPair.load(jSch, bytes, null)
            } catch (Throwable e) {
                log.warn("failed load jsch private ", e)
            }
            try {
                JSch jSch = new JSch()
                return KeyPair.load(jSch, null, bytes)
            } catch (Throwable e) {
                log.warn("failed load jsch public ", e)
            }
        } else {
            JSch jSch = new JSch()
            try {
                if (isPrivateKey) {
                    return KeyPair.load(jSch, bytes, null)
                } else {
                    return KeyPair.load(jSch, null, bytes)
                }
            } catch (Throwable e) {
                log.warn("failed load jsch common ", e)
            }
        }

        return null
    }

}
