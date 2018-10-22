package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*

import java.util.logging.Logger

@CompileStatic
class BasicCredentialsProviderMavenJrr extends BasicCredentialsProvider{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public JrrMavenHttpUtils jrrHttpUtils;

    BasicCredentialsProviderMavenJrr(JrrMavenHttpUtils mavenHttpUtils) {
        this.jrrHttpUtils = mavenHttpUtils
    }

    @Override
    void setCredentials(AuthScope authscope, Credentials credentials) {
        if(jrrHttpUtils.doLogging) {
            log.info "${authscope} ${credentials}"
        }
        super.setCredentials(authscope, credentials)
    }

    @Override
    Credentials getCredentials(AuthScope authscope) {
        Credentials credentials = super.getCredentials(authscope)
        if(jrrHttpUtils.doLogging) {
            log.info "${authscope} ${credentials}"
        }
        return credentials;
    }

    @Override
    void clear() {
        if(jrrHttpUtils.doLogging) {
            log.info "clear"
        }
        super.clear()
    }
//    void setCredentials(AuthScope authscope, Credentials credentials);
//
//    Credentials getCredentials(AuthScope authscope);
//    void clear();

}
