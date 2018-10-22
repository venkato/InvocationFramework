package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECField
import java.security.spec.ECFieldF2m
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve;
import java.util.logging.Logger;

// https://github.com/str4d/ed25519-java
// https://stackoverflow.com/questions/24121801/how-to-verify-if-the-private-key-matches-with-the-certificate
@CompileStatic
class EcPrivateKeyChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void check1( ECPrivateKey privateKey,ECPublicKey publicKey) {
        ECParameterSpec pkSpec = publicKey.getParams(), skSpec = privateKey.getParams();
        EllipticCurve skCurve = skSpec.getCurve(), pkCurve = pkSpec.getCurve();
        ECField skField = skCurve.getField(), pkField = pkCurve.getField();
        BigInteger skA = skCurve.getA(), skB = skCurve.getB();
        if (pkSpec != skSpec //
                && (pkSpec.getCofactor() != skSpec.getCofactor() //
                || !pkSpec.getOrder().equals(skSpec.getOrder()) //
                || !pkSpec.getGenerator().equals(skSpec.getGenerator()) //
                || pkCurve != skCurve //
                && (!pkCurve.getA().equals(skA) //
                || !pkCurve.getB().equals(skB) //
                || skField.getFieldSize() != pkField.getFieldSize()))) {
            assert false
        }


        ECPoint w = publicKey.getW();
        BigInteger x = w.getAffineX(), y = w.getAffineY();
        if (skField instanceof ECFieldFp) {
            BigInteger skP = ((ECFieldFp) skField).getP();
            if(pkField instanceof ECFieldFp && skP.equals(((ECFieldFp) pkField).getP()) ){
                int res12 =y.pow(2).subtract(x.pow(3)).subtract(skA.multiply(x)).subtract(skB).mod(skP).signum()
                assert res12 == 0
            }
            assert false

        }
        if (skField instanceof ECFieldF2m) {
            int m = ((ECFieldF2m) skField).getM();
            BigInteger rp = ((ECFieldF2m) skField).getReductionPolynomial();
            if (!(pkField instanceof ECFieldF2m) || m != ((ECFieldF2m) skField).getM() || !rp.equals(((ECFieldF2m) skField).getReductionPolynomial())) {
                assert false
            }
            BigInteger x2 = f2mReduce(f2mMultiply(x, x), rp, m);
            int res =  f2mReduce(f2mSum(f2mMultiply(y, y), f2mMultiply(x, y), f2mMultiply(x, x2), f2mMultiply(skA, x2), skB), rp, m).signum();
            assert res == 0
        }
        assert false
    }


    public static final BigInteger f2mSum(BigInteger... values) {
        if (values.length == 0)
            return BigInteger.ZERO;
        BigInteger result = values[0];
        for (int i = values.length - 1; i > 0; i--)
            result = result.xor(values[i]);
        return result;
    }


    public static final BigInteger f2mAdd(BigInteger a, BigInteger b) {
        return a.xor(b);
    }


    public static final BigInteger f2mSubtract(BigInteger a, BigInteger b) {
        return a.xor(b);
    }


    public static final BigInteger f2mMultiply(BigInteger a, BigInteger b) {
        BigInteger result = BigInteger.ZERO, sparse, full;
        if (a.bitCount() > b.bitCount()) {
            sparse = b;
            full = a;
        } else {
            sparse = b;
            full = a;
        }
        for (int i = sparse.bitLength(); i >= 0; i--)
            if (sparse.testBit(i))
                result = result.xor(full.shiftLeft(i));
        return result;
    }


    public static final BigInteger f2mReduce(BigInteger input, BigInteger reductionPolynom, int bitLength) {
        while (input.bitLength() > bitLength)
            input = input.xor(reductionPolynom.shiftLeft(input.bitLength() - reductionPolynom.bitLength()));
        return input;
    }

}
