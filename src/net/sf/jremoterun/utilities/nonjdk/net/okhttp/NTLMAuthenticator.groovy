package net.sf.jremoterun.utilities.nonjdk.net.okhttp

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.NTLMSchemeJrr
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.Headers;
import org.apache.http.auth.AUTH
import org.apache.http.impl.auth.NTLMEngine
import org.apache.http.impl.auth.NTLMEngineException

import java.util.logging.Logger

/** see also
 * @see sun.net.www.protocol.http.ntlm.NTLMAuthentication* @see jcifs.ntlmssp.Type1Message* https://github.com/square/okhttp/issues/206
 */

@CompileStatic
class NTLMAuthenticator implements Authenticator {

    private static Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String okSpecialHeader = "OkHttp-Preemptive"
    public static int maxCount = 5

    public NTLMEngine engine = NTLMSchemeJrr.createNTLMEngineImpl();
    public String domain;
    public String username;
    public String password;
    public String ntlmMsg1;
    public String workstation;
    public NtlmState state = NtlmState.UNINITIATED;
    public int countt = 0


    public NTLMAuthenticator(String username, String password, String domain, String workstation) throws NTLMEngineException {
        this.domain = domain;
        this.username = username;
        this.password = password;
        ntlmMsg1 = engine.generateType1Msg(domain, null);
        this.workstation = workstation
    }


    @Override
    Request authenticate(Route route, Response response) throws IOException {
        String headerValue = authenticateImpl(response)
        return response.request().newBuilder().header(AUTH.PROXY_AUTH_RESP, "NTLM ${headerValue}").build();
    }


    String authenticateImpl(Response response) throws IOException {
        if (response.code() != 407) {
            throw new IOException("bad code ${response.code()}");
        }
        Headers headers = response.headers();
        log.info "state = ${state}, headers = ${headers}"
        final List<String> proxyAuthHeaders = headers.values(AUTH.PROXY_AUTH);
        if (proxyAuthHeaders.size() == 0) {
            throw new IOException("No ${AUTH.PROXY_AUTH} header : ${headers}");
        }
        if (state == NtlmState.MSG_TYPE3_GENERATED || state == NtlmState.UNINITIATED) {
            if (proxyAuthHeaders.size() == 1) {
                if (proxyAuthHeaders.first() == okSpecialHeader) {
                    log.info "restarting and send first msg "
                    state = NtlmState.MSG_TYPE1_GENERATED;
                    return ntlmMsg1
                }
            }
        }
        if (proxyAuthHeaders.contains("NTLM")) {
            if (state != NtlmState.UNINITIATED) {
                log.info("Wrong state : ${state}")
                countt++
                if (countt > maxCount) {
                    throw new Exception("Wrong state : ${state}")
                }
            }
            log.info("generate first");
            state = NtlmState.MSG_TYPE1_GENERATED;
            return ntlmMsg1
        }
        if (state != NtlmState.MSG_TYPE1_GENERATED) {
            throw new IOException("Wrong state : ${state}")
        }
        List<String> ntlmaaa = proxyAuthHeaders.findAll { it.startsWith('NTLM ') }
        if (ntlmaaa.size() == 0) {
            return onNoHeaderFound(response, headers, proxyAuthHeaders)
        }
        String ntlmMsg3 = ntlmaaa.first();
        return doType3(ntlmMsg3)
    }

    String doType3(String ntlmMsg3) {
        String substring = ntlmMsg3.substring(5);
        state = NtlmState.MSG_TYPE2_RECEVIED;
        log.info "got ${state}, generating type3 msgs .."
        ntlmMsg3 = engine.generateType3Msg(username, password, domain, workstation, substring);
        state = NtlmState.MSG_TYPE3_GENERATED;
        log.info "sending ${state}"
        return ntlmMsg3
    }

    String onNoHeaderFound(Response response, Headers headers, List<String> proxyAuthHeaders) {
        throw new IOException("NTLM header not found ${proxyAuthHeaders}")
    }
} 