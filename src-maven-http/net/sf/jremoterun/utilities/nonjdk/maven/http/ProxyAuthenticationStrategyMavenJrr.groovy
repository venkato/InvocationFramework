package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.Header
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.HttpResponse
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthOption
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScheme
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.MalformedChallengeException
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.CredentialsProvider
import org.apache.maven.wagon.providers.http.httpclient.client.protocol.HttpClientContext
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext

import java.util.logging.Logger

@CompileStatic
class ProxyAuthenticationStrategyMavenJrr extends ProxyAuthenticationStrategy{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrMavenHttpUtils jrrHttpUtils;


    ProxyAuthenticationStrategyMavenJrr(JrrMavenHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        return super.isAuthenticationRequested(authhost, response, context)
    }

    @Override
    Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Map<String, Header> challenges = super.getChallenges(authhost, response, context)
        if(jrrHttpUtils. doLogging) {
            log.info "${authhost} ${challenges}"
        }
        return challenges
    }

    @Override
    Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        if (jrrHttpUtils.credentials instanceof NTCredentials) {
            CredentialsProvider credentialsProvider = clientContext.getCredentialsProvider()
            AuthScope authScope = new AuthScope(jrrHttpUtils.proxyHost, jrrHttpUtils.proxyPort)
            Credentials credentials = credentialsProvider.getCredentials(authScope);
            if(credentials==null){
                if(jrrHttpUtils.doLogging) {
                    log.info "pushing credentials for ${authScope}"
                }
                credentialsProvider.setCredentials(authScope,jrrHttpUtils.credentials)
                if(jrrHttpUtils.proxyip!=jrrHttpUtils.proxyHost) {
                    AuthScope authScope2 = new AuthScope(jrrHttpUtils.proxyip, jrrHttpUtils.proxyPort)
                    if(jrrHttpUtils.doLogging) {
                        log.info "pushing credentials for ${authScope2}"
                    }
                    credentialsProvider.setCredentials(authScope2, jrrHttpUtils.credentials)
                }
                context = clientContext;
            }
        }
        Queue<AuthOption> select = super.select(challenges, authhost, response, context)
        if(jrrHttpUtils.doLogging) {
            log.info "${challenges} ${select}"
        }
        return select
    }



    @Override
    void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        if(jrrHttpUtils.doLogging) {
            log.info "good ${authhost}"
        }
        super.authSucceeded(authhost, authScheme, context)
    }

    @Override
    void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        if(jrrHttpUtils.doLogging) {
            log.info "failed ${authhost}"
        }
        super.authFailed(authhost, authScheme, context)
    }
}
