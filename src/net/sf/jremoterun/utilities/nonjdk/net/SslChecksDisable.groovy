package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import java.security.SecureRandom;
import java.util.logging.Logger;

@CompileStatic
class SslChecksDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
    public static HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()


    static void allowAll() {

        HttpsURLConnection.setDefaultHostnameVerifier(new SslHostNameVerifierAllowAll())

        TrustManager trustManager = new SslAllTrustManager()
        TrustManager[] trustManagers = [trustManager]

        SSLContext sSLContext = SSLContext.getInstance('TLS')
        sSLContext.init(null, trustManagers, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory())
    }

}
