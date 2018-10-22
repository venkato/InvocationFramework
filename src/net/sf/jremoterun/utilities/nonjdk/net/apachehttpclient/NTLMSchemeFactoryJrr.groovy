package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.auth.AuthScheme
import org.apache.http.impl.auth.NTLMSchemeFactory
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext;

import java.util.logging.Logger;

@CompileStatic
class NTLMSchemeFactoryJrr extends NTLMSchemeFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrHttpUtils jrrHttpUtils;
    public volatile NTLMSchemeJrr lastNTLMSchemeJrr;

    NTLMSchemeFactoryJrr(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    AuthScheme newInstance(HttpParams params) {
        NTLMSchemeJrr ntlmJrr  =  new NTLMSchemeJrr(jrrHttpUtils)
        lastNTLMSchemeJrr = ntlmJrr
        return ntlmJrr
    }

    @Override
    AuthScheme create(HttpContext context) {
        NTLMSchemeJrr ntlmJrr  =  new NTLMSchemeJrr(jrrHttpUtils)
        lastNTLMSchemeJrr = ntlmJrr
        return ntlmJrr
    }
}
