package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.settings.TimeoutConstraint

//import org.apache.ivy.core.settings.TimeoutConstraint
import org.apache.ivy.util.CopyProgressListener
import org.apache.ivy.util.url.BasicURLHandler
import org.apache.ivy.util.url.URLHandler
import org.apache.ivy.util.url.URLHandlerRegistry;

import java.util.logging.Logger;



@CompileStatic
class JrrIvyURLHandler extends BasicURLHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static URLHandler beforeS;
    public URLHandler before2;



    static JrrIvyURLHandler setHandlerForce() {
        JrrIvyURLHandler handler = new JrrIvyURLHandler()
        handler.before2 = URLHandlerRegistry.getDefault()
        URLHandlerRegistry.setDefault(handler)
        return handler;
    }


    static void setHandler() {
        if (beforeS == null) {
            JrrIvyURLHandler handler = setHandlerForce()
            beforeS = handler.before2
        }else{
            log.info "custom url handler already set : ${beforeS.getClass().getName()}"
        }
    }

    boolean isNeedLogUrl(URL url){
        if(url.toString().startsWith('jar:file:')){
            return false;
        }
        if(url.toString().startsWith('file:')){
            return false
        }
        return true
    }

    @Override
    URLInfo getURLInfo(URL url, int timeoutConstraint) {
        if(isNeedLogUrl(url)) {
            log.info "downloading : ${url}"
        }
        return super.getURLInfo(url, timeoutConstraint)
    }

    @Override
    void download(URL src, File dest, CopyProgressListener listener, TimeoutConstraint timeoutConstraint) throws IOException {
        // this used in ivy 2.5.0
        if(isNeedLogUrl(src)) {
            log.info2(src)
        }
        super.download(src, dest, listener, timeoutConstraint)
    }

    @Override
    InputStream openStream(URL url) throws IOException {
        return super.openStream(url)
    }

    @Override
    InputStream openStream(URL url, TimeoutConstraint timeoutConstraint) throws IOException {
        if(isNeedLogUrl(url)) {
            log.info2(url)
        }
        return super.openStream(url, timeoutConstraint)
    }

    @Override
    void download(URL src, File dest, CopyProgressListener listener) throws IOException {
        if(isNeedLogUrl(src)) {
            log.info2(src)
        }
        super.download(src, dest, listener)
    }

    @Override
    void upload(File src, URL dest, CopyProgressListener listener) throws IOException {
        log.info2(dest)
        super.upload(src, dest, listener)
    }
}
