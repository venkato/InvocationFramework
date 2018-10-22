package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate;
import java.util.logging.Logger;


/**
 * @see org.apache.commons.net.util.TrustManagerUtils#getAcceptAllTrustManager()
 */
@CompileStatic
class SslAllTrustManager implements X509TrustManager {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    @Override
    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0]
    }
}
