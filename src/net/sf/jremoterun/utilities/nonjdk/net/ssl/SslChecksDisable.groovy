package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import java.security.SecureRandom
import java.util.logging.Logger;

@CompileStatic
class SslChecksDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
    public static HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
    public static SSLContext sslContextDefault = SSLContext.getDefault()
    public static String defaultProtocol = 'TLS'


    static void allowAll() {
        HttpsURLConnection.setDefaultHostnameVerifier(new SslHostNameVerifierAllowAll())
        SSLContext sSLContext = createAllTrustSslContext()
        SSLContext.setDefault(sSLContext)
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory())
    }

    static SSLContext createAllTrustSslContext(){
        TrustManager trustManager = new SslAllTrustManager()
        TrustManager[] trustManagers = [trustManager]

        SSLContext sSLContext = SSLContext.getInstance(defaultProtocol)
        sSLContext.init(null, trustManagers, new SecureRandom())
        return sSLContext;
    }



}
