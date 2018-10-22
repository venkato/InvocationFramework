package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import org.apache.commons.codec.binary.Base64

import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Provider
import java.security.PublicKey
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

@CompileStatic
class AsymetricKeyLoader3 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static Key loadKey(byte[] bytes, String type, Provider provider) {
        String text = new String(bytes,'UTF8')
        List<String> lines = text.readLines()
        String firstLine = lines[0]
        if (firstLine.contains('BEGIN RSA PRIVATE KEY')) {
            String lastLine = lines.last()
            assert lastLine.contains('END RSA PRIVATE KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            if (type == null) {
                type = KeyType.RSA.name()
            }
            return loadPrivateKey(decodeBase64, type, provider)
        }
        if (firstLine.contains('BEGIN DSA PRIVATE KEY')) {
            String lastLine = lines.last()
            assert lastLine.contains('END DSA PRIVATE KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            if (type == null) {
                type = KeyType.DSA.name()
            }
            return loadPrivateKey(decodeBase64, type, provider)
        }
        if (firstLine.contains('BEGIN PUBLIC KEY')) {
            String lastLine = lines.last()
            assert lastLine.contains('END PUBLIC KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            return loadPublicKey(decodeBase64, type, provider)
        }
        throw new Exception("unkown type")
    }

    static PrivateKey loadPrivateKey(byte[] keyBytes, String keyType, Provider provider) {
        if(keyType==null){
            throw new Exception("set key keyType")
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf;
        if (provider == null) {
            kf = KeyFactory.getInstance(keyType);
        } else {
            kf = KeyFactory.getInstance(keyType, provider);
        }
        PrivateKey privateKey = kf.generatePrivate(spec);
        return privateKey;
    }

    static PublicKey loadPublicKey(byte[] keyBytes, String type, Provider provider) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf;
        if (provider == null) {
            kf = KeyFactory.getInstance(type);
        } else {
            kf = KeyFactory.getInstance(type, provider);
        }
        PublicKey publicKey = kf.generatePublic(spec);
        return publicKey;
    }

    static  X509Certificate loadX509Certificate(byte[] bytes){
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais =new ByteArrayInputStream(bytes)
        X509Certificate certificate = fact.generateCertificate(bais) as X509Certificate;
        bais.close()
        return certificate
    }




    static Provider[] receiveProviders() {
        return Security.getProviders();
    }

    static Map<String, Set<String>> getSecurityServices() {
        return JrrUtilities.getSecurityServices();
    }


}
