package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import org.apache.commons.codec.binary.Base64
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.logging.Logger

@CompileStatic
class AsymetricKeyBC2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    static Object loadKeyBc(byte[] bytes) {
        PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(bytes)));
        return pemParser.readObject();
    }
    static Object loadKeyBcUseful(byte[] bytes) {
        PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(bytes)));
        Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        if (object instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) object;
            KeyPair keyPair = converter.getKeyPair(pemKeyPair);
            return keyPair
        }
        if (object instanceof SubjectPublicKeyInfo) {
            SubjectPublicKeyInfo  subjectPublicKeyInfo= (SubjectPublicKeyInfo) object;
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo)
            return publicKey
        }
        if (object instanceof PrivateKeyInfo) {
            PrivateKeyInfo  subjectPublicKeyInfo= (PrivateKeyInfo) object;
            return converter.getPrivateKey(subjectPublicKeyInfo)
        }
        if (object instanceof X509CertificateHolder) {
            X509CertificateHolder certificateHolder = (X509CertificateHolder) object;
            return new JcaX509CertificateConverter().setProvider( JavaSecurityProviders.BC.name() )
                    .getCertificate( certificateHolder );
        }
        return object;
    }
    
    static KeyPair loadKeyPair(byte[] bytes) {
        PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(bytes)));
        PEMKeyPair pemKeyPair = (PEMKeyPair)pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        KeyPair keyPair = converter.getKeyPair(pemKeyPair);
        return keyPair;
    }


}
