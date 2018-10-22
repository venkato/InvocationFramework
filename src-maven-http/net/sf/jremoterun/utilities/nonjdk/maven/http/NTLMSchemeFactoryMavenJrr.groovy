package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScheme
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.auth.UsernamePasswordCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.AuthenticationStrategy
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes
import org.apache.maven.wagon.providers.http.httpclient.config.Registry
import org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMSchemeFactory
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.impl.conn.SystemDefaultRoutePlanner
import org.apache.maven.wagon.providers.http.httpclient.params.HttpParams
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext

import java.util.logging.Logger

@CompileStatic
class NTLMSchemeFactoryMavenJrr extends NTLMSchemeFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrMavenHttpUtils jrrHttpUtils;
    public volatile NTLMSchemeMavenJrr lastNTLMSchemeJrr;

    NTLMSchemeFactoryMavenJrr(JrrMavenHttpUtils  jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    AuthScheme newInstance(HttpParams params) {
        NTLMSchemeMavenJrr ntlmJrr  =  new NTLMSchemeMavenJrr(jrrHttpUtils)
        lastNTLMSchemeJrr = ntlmJrr
        return ntlmJrr
    }

    @Override
    AuthScheme create(HttpContext context) {
        NTLMSchemeMavenJrr ntlmJrr  =  new NTLMSchemeMavenJrr(jrrHttpUtils)
        lastNTLMSchemeJrr = ntlmJrr
        return ntlmJrr
    }
}
