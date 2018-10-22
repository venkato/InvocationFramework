package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import org.apache.commons.codec.binary.Base64

import java.security.*
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.interfaces.ECKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.logging.Logger

@CompileStatic
class AsymetricKeyChecker2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void checkGeneric(File privateKeyF, File publicKeyF, KeyType keyType) {
        PrivateKey privateKey = AsymetricKeyLoaderAny.loadKeyAny(privateKeyF, keyType, true) as PrivateKey
        assert privateKey != null
        PublicKey publicKey = AsymetricKeyLoaderAny.loadKeyAny(publicKeyF, keyType, false) as PublicKey
        assert publicKey != null
        checkGeneric(privateKey, publicKey)
    }

    static void checkGeneric(PrivateKey privateKey, PublicKey publicKey) {
        assert privateKey != null
        assert publicKey != null
        boolean checked = false;
        if (privateKey instanceof RSAPrivateKey) {
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
            checkRsaKeyMatch1(rsaPrivateKey, publicKey as RSAPublicKey)
            checkRsaKeyMatch3(rsaPrivateKey, publicKey as RSAPublicKey)
            checked = true;
        }
        if (privateKey instanceof RSAPrivateCrtKey) {
            RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey) privateKey;
            checkRsaKeyMatch2(rsaPrivateCrtKey, publicKey as RSAPublicKey)
            checked = true;
        }
        if (privateKey instanceof DSAPrivateKey) {
            DSAPrivateKey dsaPrivateKey = (DSAPrivateKey) privateKey;
            checkDsaKeyMatch(dsaPrivateKey, publicKey as DSAPublicKey)
            checkDsaKeyMatch2(dsaPrivateKey, publicKey as DSAPublicKey)
            checked = true;
        }
        if (privateKey instanceof ECKey) {
            ECKey ecKey = (ECKey) privateKey;
            checkKeyMatch4(ecKey, publicKey as ECKey)
            checked = true;
        }
        if (privateKey instanceof ECPrivateKey) {
            ECPrivateKey eCPrivateKey = (ECPrivateKey) privateKey;
            EcPrivateKeyChecker.check1(eCPrivateKey, publicKey as ECPublicKey)
            checked = true;
        }

        assert checked
    }

    static void checkDsaKeyMatch2(DSAPrivateKey priKey, DSAPublicKey pubKey) {
        DSAParams params = priKey.getParams();
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger g = params.getG();
        BigInteger x = priKey.getX();
        BigInteger y = pubKey.getY();
        log.info("Is p a prime? " + p.isProbablePrime(200));
        log.info("Is q a prime? " + q.isProbablePrime(200));
        BigInteger subtract = p.subtract(BigInteger.ONE).mod(q)
        log.info("Is p-1 mod q == 0? " + subtract);
        assert subtract==BigInteger.ZERO
        BigInteger modPow = g.modPow(q, p);
        log.info("Is g**q mod p == 1? " + modPow);
        assert modPow == BigInteger.ONE
        log.info("Is q > x? " + (q.compareTo(x) == 1));
        assert q >x
        BigInteger modPow2 = g.modPow(x, p)
        log.info("Is g**x mod p == y? " + modPow2.equals(y));
        assert modPow2 == y
    }

    static void checkDsaKeyMatch(DSAPrivateKey dsaPrivateKey, DSAPublicKey dsaPublicKey) {
        assert dsaPrivateKey.getParams() == dsaPublicKey.getParams()
    }

    static void checkRsaKeyMatch3(RSAPrivateKey rsaPrivateKey, RSAPublicKey rsaPublicKey) {
        assert rsaPublicKey.getModulus().equals(rsaPrivateKey.getModulus())

        BigInteger pow = BigInteger.valueOf(2).modPow(rsaPublicKey.getPublicExponent()
                .multiply(rsaPrivateKey.getPrivateExponent()).subtract(BigInteger.ONE),
                rsaPublicKey.getModulus());
        assert pow == BigInteger.ONE
    }

    // TODO works ?
    static void checkKeyMatch4(ECKey privateKey, ECKey publicKey) {
        assert privateKey.getParams() == publicKey.getParams();
    }

    static void checkRsaKeyMatch1(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        assert privateKey.getModulus() == publicKey.getModulus();
    }


    static void checkRsaKeyMatch2(RSAPrivateCrtKey privateKey, RSAPublicKey publicKey) {
        assert privateKey.getModulus() == publicKey.getModulus();
        assert privateKey.getPublicExponent() == publicKey.getPublicExponent();
    }


}
