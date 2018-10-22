package net.sf.jremoterun.utilities.nonjdk.net.ssl.bc

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.bouncycastle.tls.DefaultTlsClient
import org.bouncycastle.tls.ServerOnlyTlsAuthentication
import org.bouncycastle.tls.TlsAuthentication
import org.bouncycastle.tls.TlsClient
import org.bouncycastle.tls.TlsClientProtocol
import org.bouncycastle.tls.TlsServerCertificate
import org.bouncycastle.tls.crypto.TlsCrypto
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto

import java.security.SecureRandom;
import java.util.logging.Logger;

@CompileStatic
class SslConnectionInspectBc {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // Depends on:
//    bctls_jdk15to18,
//    bcprov_jdk15to18,
//    bcutil_jdk15to18,

    public List<TlsServerCertificate> serverCertificates = []
    public Socket socket;
    public TlsClientProtocol tlsClientProtocol
    public TlsCrypto tlsCrypto;
    public TlsClient tlsClient

    void check(String host, int port) {
        socket = new Socket(host, port);
        connect()
    }

    void connect() {
        tlsClientProtocol = new TlsClientProtocol(
                socket.getInputStream(), socket.getOutputStream());
        if (tlsCrypto == null) {
            tlsCrypto = new BcTlsCrypto(new SecureRandom())
        }
        if (tlsClient == null) {
            tlsClient = new DefaultTlsClient(tlsCrypto) {
                public TlsAuthentication getAuthentication() throws IOException {
                    return new ServerOnlyTlsAuthentication() {

                        @Override
                        void notifyServerCertificate(TlsServerCertificate serverCertificate) throws IOException {
                            serverCertificates.add(serverCertificate)
                        }
                    };
                }
            }
        }
        tlsClientProtocol.connect(tlsClient);

        //InputStream stream = tlsClientProtocol.getInputStream()
    }

}
