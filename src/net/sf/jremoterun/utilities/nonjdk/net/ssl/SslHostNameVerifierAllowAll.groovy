package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 @see org.apache.http.conn.ssl.NoopHostnameVerifier
 */
@CompileStatic
class SslHostNameVerifierAllowAll implements HostnameVerifier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    boolean verify(String s, SSLSession sslSession) {
        return true
    }


}
