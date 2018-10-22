package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.maven.wagon.providers.http.httpclient.Header
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthenticationException
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.auth.UsernamePasswordCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.AuthenticationStrategy
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes
import org.apache.maven.wagon.providers.http.httpclient.config.Registry
import org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMEngine
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMScheme
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMSchemeFactory
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.impl.conn.SystemDefaultRoutePlanner

import java.util.logging.Logger

@CompileStatic
class NTLMSchemeMavenJrr extends NTLMScheme{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef clRefEngineImpl = new ClRef('org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMEngineImpl')

    public JrrMavenHttpUtils jrrHttpUtils;

    NTLMSchemeMavenJrr(JrrMavenHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    NTLMSchemeMavenJrr(NTLMEngine engine, JrrMavenHttpUtils jrrHttpUtils) {
        super(engine)
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        if(jrrHttpUtils.doLogging) {
            log.info "proxy auth state = ${getState1()}"
        }
        return super.authenticate(credentials, request)
    }

    public Object getState1(){
        return JrrClassUtils.getFieldValue(this,'state')
    }



    static NTLMEngine createNTLMEngineImpl(){
        clRefEngineImpl.newInstance3() as NTLMEngine
    }

}
