package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.crypto.Cipher;
import java.util.logging.Logger;

@CompileStatic
enum CipherMode {


    encrypt(Cipher.ENCRYPT_MODE),
    wrap(Cipher.WRAP_MODE),
    unwrap(Cipher.UNWRAP_MODE),
    decrypt(Cipher.DECRYPT_MODE);


    int javaMode;

    CipherMode(int javaMode) {
        this.javaMode = javaMode
    }
}
