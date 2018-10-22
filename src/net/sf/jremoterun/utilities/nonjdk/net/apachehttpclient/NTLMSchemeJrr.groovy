package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.auth.AuthenticationException
import org.apache.http.auth.Credentials
import org.apache.http.impl.auth.NTLMEngine
import org.apache.http.impl.auth.NTLMScheme

import java.util.logging.Logger;

@CompileStatic
class NTLMSchemeJrr extends NTLMScheme{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef clRefEngineImpl = new ClRef('org.apache.http.impl.auth.NTLMEngineImpl')

    public JrrHttpUtils jrrHttpUtils;

    NTLMSchemeJrr(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    NTLMSchemeJrr(NTLMEngine engine, JrrHttpUtils jrrHttpUtils) {
        super(engine)
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        log.info "proxy auth state = ${getState1()}"
        return super.authenticate(credentials, request)
    }

    public Object getState1(){
        return JrrClassUtils.getFieldValue(this,'state')
    }



    static NTLMEngine createNTLMEngineImpl(){
        clRefEngineImpl.newInstance3() as NTLMEngine
    }

}
