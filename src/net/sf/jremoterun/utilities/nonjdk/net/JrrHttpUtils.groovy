package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.BasicCredentialsProviderJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.NTLMSchemeFactoryJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ProxyAuthenticationStrategyJrr
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslChecksDisable
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslHostNameVerifierAllowAll
import org.apache.http.Header
import org.apache.http.auth.AuthSchemeProvider
import org.apache.http.auth.AuthScope
import org.apache.http.auth.NTCredentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.AuthenticationStrategy
import org.apache.http.client.config.AuthSchemes
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.ssl.SSLContextBuilder

import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenHttpUtils
 */
@CompileStatic
public class JrrHttpUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public HttpClientBuilder httpClientBuilder = HttpClients.custom()
    public CloseableHttpClient httpClient1;
    public AuthSchemeProvider ntlmSchemeFactory = new NTLMSchemeFactoryJrr(this)
    public AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategyJrr();
    public BasicCredentialsProvider credentialsProvider1 = new BasicCredentialsProviderJrr();


    void createClient() {
        httpClient1 = httpClientBuilder.build();
    }



    void setCred(NTCredentials credentials , String proxyHost, int proxyPort) {
        credentialsProvider1.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);
        InetAddress name = InetAddress.getByName(proxyHost)
        String ipAddress = name.getHostAddress()
        if(ipAddress!=proxyHost){
            credentialsProvider1.setCredentials(new AuthScope(ipAddress, proxyPort), credentials);
        }
    }

    NTCredentials createNTCredentials(String user,String password,String domain){
        return new NTCredentials(user,password,null,domain);
    }


    /**
     * Set ProxySelector.getDefault() before !!
     */
    void addProxyNtlmAuth3(NTCredentials credentials, String proxyHost, int proxyPort) {
        setCred(credentials,proxyHost,proxyPort)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
        setRouterPlanner()
    }

    void setRouterPlanner() {
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault())
        httpClientBuilder.setRoutePlanner(routePlanner)
    }

    void addRedirectionNon() {
        httpClientBuilder.setRedirectStrategy(new RedirectStrategyNone())
    }


    void sslCheckDisable() {
        httpClientBuilder.setSSLHostnameVerifier(new SslHostNameVerifierAllowAll());
        httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(SslChecksDisable.createAllTrustSslContext()));
    }





//    @Deprecated
//    void addProxyNtlmAuth(String user, String passport, String domain, String proxyHost, int proxyPort) {
//        NTCredentials credentials = new NTCredentials(user, passport, null, domain)
//        setCred(credentials,proxyHost,proxyPort)
//        HttpHost proxyHttpHost1 = new HttpHost(proxyHost, proxyPort);
//        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
//        httpClientBuilder.setProxy(proxyHttpHost1)
//        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
//        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
//        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
//    }

    List<X509Certificate> getCertificatesFromHost(String host) {
        X509Certificate[] chains
        SSLContextBuilder builder = new SSLContextBuilder();
        TrustStrategy trustStrategy = new TrustStrategy() {

            @Override
            boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                chains = chain;
                return true;
            }
        }
        // for cert auth put private key certificate here for loadTrustMaterial method
        builder.loadTrustMaterial(null, trustStrategy);
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

        httpClientBuilder.setSSLSocketFactory(sslsf);

        String urll
        if (host.startsWith('https://')) {
            urll = host
        } else {
            urll = "https://${host}"
        }
        HttpGet httpGet = new HttpGet(urll);
        createClient();
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        response.close()

        if (chains == null) {
            throw new NullPointerException("No certificates")
        }
        return chains.toList();
    }



    @Deprecated
    void createClientCred(String host, int port, String username, String password) {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider()
        basicCredentialsProvider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(username, password));
        httpClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider);
    }


    Header getHeader(URL url, String headerName) {
        createClient();
        HttpGet httpGet = new HttpGet(url.toString());
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        try {
            assert response.getStatusLine().getStatusCode() == 200: url
            Header[] headers = response.getAllHeaders()
            Header header = response.getFirstHeader(headerName)
            if (header == null) {
                throw new IllegalStateException("header not found ${headerName}, headers : ${headers.toList().collect { it.getName() }} ${url}")
            }
            return header
        } finally {
            response.close()
        }
    }


}
