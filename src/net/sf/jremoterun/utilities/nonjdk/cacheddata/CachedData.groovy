package net.sf.jremoterun.utilities.nonjdk.cacheddata

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.codec.binary.Base64

import java.security.MessageDigest;
import java.util.logging.Logger;

@CompileStatic
class CachedData {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static MessageDigest messageDigest = MessageDigest.getInstance('SHA-256');


    static String getDigest(String query) {
        byte[] digest = messageDigest.digest(query.getBytes())
        return new String(Base64.encodeBase64(digest)).replace('/', '-').replace('/', '-')
    }


}
