package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
import net.sf.jremoterun.utilities.nonjdk.crypto.CipherMode
import org.apache.commons.io.IOUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.Security
import java.util.logging.Logger

@CompileStatic
class EncDec {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass()

    EncInfo encInfo

    static EncInfo loadConfig(File encInfoFile) {
        JrrGroovyScriptRunner r = new JrrGroovyScriptRunner()
        EncInfo encInfo2 = new EncInfo()
        r.loadSettingsWithParam(encInfoFile, encInfo2)
        return encInfo2;
    }

    void encDec1(File inf, File outF, CipherMode mode, File encInfo) {
        EncInfo encInfo2 = loadConfig(encInfo)
        encDec2(inf, outF, mode, encInfo2)
    }

    void encDec2(File inf, File outF, CipherMode mode, EncInfo encInfo) {
        assert inf != outF
        this.encInfo = encInfo
        Security.addProvider(new BouncyCastleProvider())
        SecretKey key2 = getKey()
        Cipher cipher = Cipher.getInstance(encInfo.cipherAlgo, encInfo.provider)
        IvParameterSpec spec = new IvParameterSpec(encInfo.iv.toByteArray())
//        cipher.init(mode, key2)
        cipher.init(mode.javaMode, key2, spec)
        CipherInputStream stream = new CipherInputStream(inf.newInputStream(), cipher)
        BufferedOutputStream stream1 = outF.newOutputStream()
        IOUtils.copy(stream, stream1)
        stream1.flush()
        stream.close()
        stream1.close()
//        log.info new JavaBeanStore().saveS(encInfo)
        log.info "finished"
    }

    SecretKey getKey() {
        String pass = encInfo.pass
//        log.info "pass = ${pass}"
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray(), encInfo.keySalt.toByteArray(), encInfo.iter, encInfo.keyLen)
        SecretKeyFactory skf = SecretKeyFactory.getInstance(encInfo.keyParam, encInfo.provider)
//        log.info  skf.getProvider().getName()
        SecretKey secret = skf.generateSecret(keySpec)
        SecretKey secret2 = new SecretKeySpec(secret.getEncoded(), "AES")
        return secret2

    }


}
