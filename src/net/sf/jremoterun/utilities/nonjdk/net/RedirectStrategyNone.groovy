package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.ProtocolException
import org.apache.http.client.RedirectStrategy
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.protocol.HttpContext;

import java.util.logging.Logger;

@CompileStatic
class RedirectStrategyNone implements RedirectStrategy{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        log.info "isRedirected ${response}"
        return false
    }

    @Override
    HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        log.info "getRedirect ${response}"
        return null
    }
}
