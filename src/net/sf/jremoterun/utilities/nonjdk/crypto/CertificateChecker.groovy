package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import org.apache.commons.lang3.SystemUtils

import java.security.KeyStore
import java.security.Principal
import java.security.cert.Certificate
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class CertificateChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String customTrustedStorePath = 'javax.net.ssl.trustStore'
    public static String defaultJksPassword = 'changeit'
    public KeyStore keyStore1;

    CertificateChecker() {
        this(loadDefaultKetStoreJks(getDefaultJavaCertPath(),defaultJksPassword))
        // more props see in
        new ClRef('org.apache.http.impl.client.HttpClientBuilder')
    }


    CertificateChecker(KeyStore keyStore) {
        this.keyStore1 = keyStore
        if(keyStore==null){
            throw new NullPointerException('key store is null')
        }
    }

    static File getDefaultJavaCertPath(){
        File javaHome = SystemUtils.getJavaHome()
        File file = new File(javaHome, 'lib/security/cacerts')
        assert file.exists()
        return file
    }

    static KeyStore loadDefaultKetStoreJks(File file, String password) {
        assert file.exists()
        KeyStore keyStore = KeyStore.getInstance('JKS')
        BufferedInputStream inputStream = file.newInputStream()
        try {
            keyStore.load(inputStream, password.toCharArray())
        } catch (Exception e) {
            try {
                inputStream.close()
            } catch (Exception e2) {
                log.info2(e2)
            }
            throw e;
        }
        return keyStore;
    }

    void addCertificate(X509Certificate certificate){
        String commonName = getCommonName(certificate.getSubjectDN())
        keyStore1.setCertificateEntry(commonName,certificate);
    }


    void saveKeyStore(File file, String password) {
        FileRotate.rotateFile(file,20)
        DataOutputStream outputStream = file.newDataOutputStream()
        try {
            keyStore1.store(outputStream, password.toCharArray())
        } catch (Exception e) {
            try {
                outputStream.flush()
                outputStream.close()
            } catch (Exception e2) {
                log.info2(e2)
            }
            throw e;
        }

    }


    X509Certificate getCertificateFromKeyStoreByName(String neededCommonName) {
        X509Certificate matched;
        List<String> aliases = keyStore1.aliases().toList();
        aliases.each {
            try {
                Certificate certificate1 = keyStore1.getCertificate(it)
                if (certificate1 instanceof X509Certificate) {
                    X509Certificate x509Certificate = (X509Certificate) certificate1;
                    if (isGoodCertificate(it, x509Certificate, neededCommonName)) {
                        if (matched == null) {
                            matched = x509Certificate;
                        } else {
                            throw new Exception("found many matched certificates : ${certificate1} , ${matched}")
                        }
                    }
                }
            }catch(Exception e){
                log.info "failed iterate over ${it} : ${e}"
                throw e;
            }

        }
        return matched;
    }

    boolean isGoodCertificate(String alias, X509Certificate certificate, String neededCommonName) {
        if (certificate == null) {
            return false
        }

        X509Certificate x509Certificate = (X509Certificate) certificate;
        Principal subjectDN = x509Certificate.getSubjectDN();
        String commonName1 = getCommonName(subjectDN)
        return commonName1 == neededCommonName
    }

    void checkCertificate(X509Certificate certificate) {
        String issuerDn = getCommonName(certificate.getIssuerDN())
        X509Certificate certCheckBy = getCertificateFromKeyStoreByName(issuerDn)
        if (certCheckBy == null) {
            throw new Exception("certificate not found : ${issuerDn}")
        }
        certificate.verify(certCheckBy.getPublicKey())
    }


    String getCommonName(Principal subjectDN) {
        String string = subjectDN.toString();
        if (string == null) {
            return null
        }
        String cn;
        List<String> tokenizeL = string.tokenize(',')
        tokenizeL.each {
            String el1 = it;
            if (el1.contains('=')) {
                List<String> sepp = el1.trim().tokenize('=')
                if (sepp.size() == 2 && sepp[0] == 'CN') {
                    String cnValue = sepp[1]
                    if (cn == null) {
                        cn = cnValue
                    } else {
                        if (cn != cnValue) {
                            throw new Exception("duplicated cn : ${cnValue} , ${cn} ")
                        }
                    }
                }
            }
        }
        return cn;
    }


}
