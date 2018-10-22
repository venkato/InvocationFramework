package net.sf.jremoterun.utilities.nonjdk.net.ssl;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class SslConnectionInspect {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String tlsProtocolVersion = 'TLSv1.2'
    public List<X509Certificate> chains = []
    public SSLContext sslContext
    public SSLSocketFactory sslSocketFactory;
    public SSLSocket socket;

    public X509TrustManager trustManager = new X509TrustManager() {

        @Override
        void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            chains.addAll(chain.toList())
        }

        @Override
        void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            chains.addAll(chain.toList())
        }

        @Override
        X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0]
        }
    }

    void createSslContext() {
        sslContext = SSLContext.getInstance(tlsProtocolVersion);
        TrustManager[] tms = [trustManager]
        sslContext.init(null, tms, null)
    }

    void prepare() {
        if (sslContext == null) {
            createSslContext()
        }
        if (sslSocketFactory == null) {
            sslSocketFactory = sslContext.getSocketFactory()
        }

    }

    void check(String hostName, int port) {
        prepare()
        socket = sslSocketFactory.createSocket(hostName, port) as SSLSocket;
        startHandshake()
    }

    void startHandshake() {
        socket.startHandshake()
    }


    SSLSession getSslSession(){
        socket.getSession()
    }
}
