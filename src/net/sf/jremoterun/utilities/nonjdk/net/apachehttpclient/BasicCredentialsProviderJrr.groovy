package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.util.logging.Logger;

@CompileStatic
class BasicCredentialsProviderJrr extends BasicCredentialsProvider{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void setCredentials(AuthScope authscope, Credentials credentials) {
        log.info "${authscope} ${credentials}"
        super.setCredentials(authscope, credentials)
    }

    @Override
    Credentials getCredentials(AuthScope authscope) {
        Credentials credentials = super.getCredentials(authscope)
        log.info "${authscope} ${credentials}"
        return credentials;
    }

    @Override
    void clear() {
        log.info "clear"
        super.clear()
    }

}
