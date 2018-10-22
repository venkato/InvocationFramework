package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.auth.UsernamePasswordCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.AuthenticationStrategy
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes
import org.apache.maven.wagon.providers.http.httpclient.config.Registry
import org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMSchemeFactory
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.impl.conn.SystemDefaultRoutePlanner

import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
 */
@CompileStatic
public class JrrMavenHttpUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClRef clRef = new ClRef('org.apache.maven.wagon.providers.http.wagon.shared.AbstractHttpClientWagon')

    public HttpClientBuilder httpClientBuilder = HttpClients.custom()
    public CloseableHttpClient httpClient1;
    public AuthSchemeProvider ntlmSchemeFactory = new NTLMSchemeFactoryMavenJrr(this)
    public AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategyMavenJrr(this);
    public BasicCredentialsProvider credentialsProvider1 = new BasicCredentialsProviderMavenJrr(this);
    public Credentials credentials;
    public String proxyHost
    public String proxyip
    public int proxyPort
    public boolean doLogging = true


    void createClient() {
        httpClient1 = httpClientBuilder.build();
    }

    void setNTCredentials(String username,String password,String domain){
        credentials = new NTCredentials(username,password,null,domain)
    }

    @Deprecated
    void setCred(NTCredentials credentials, String proxyHost, int proxyPort) {
        this.credentials = credentials;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        setCred2()
    }

    void setCred2() {
        credentialsProvider1.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);
        InetAddress name = InetAddress.getByName(proxyHost)
        proxyip = name.getHostAddress()
        if (proxyip != proxyHost) {
            credentialsProvider1.setCredentials(new AuthScope(proxyip, proxyPort), credentials);
        }
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
        setRouterPlanner()
    }

    @Deprecated
    void addProxyNtlmAuth3(NTCredentials credentials, String proxyHost, int proxyPort) {
        setCred(credentials, proxyHost, proxyPort)
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


    void setRef() {
        createClient()
        JrrClassUtils.setFieldValue(clRef, 'httpClient', httpClient1);
    }


}
