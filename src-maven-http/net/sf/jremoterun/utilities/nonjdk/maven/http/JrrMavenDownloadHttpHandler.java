package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.providers.http.httpclient.HttpException;
import org.apache.maven.wagon.providers.http.httpclient.StatusLine;
import org.apache.maven.wagon.providers.http.httpclient.client.methods.CloseableHttpResponse;
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


// Replace in META-INF\plexus\components.xml  from  org.apache.maven.wagon.providers.http.HttpWagon
// to this class

@CompileStatic
public class JrrMavenDownloadHttpHandler extends HttpWagon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static int cnt1=12;
    public static List<Integer> fineHttpStatusCodes = new ArrayList<>();

    static {
        fineHttpStatusCodes.add(200);
    }


    @Override
    protected CloseableHttpResponse execute(HttpUriRequest httpMethod) throws HttpException, IOException {
        try {
            CloseableHttpResponse response = super.execute(httpMethod);
            onResult(httpMethod,response);
            return response;
        } catch (HttpException|IOException e) {
            log.log(Level.WARNING, httpMethod.getMethod() + " " + httpMethod.getURI() + " failed : ", e);
            throw e;
        }
    }

    void onResult(HttpUriRequest httpMethod,CloseableHttpResponse response){
        StatusLine statusLine = response.getStatusLine();
        if(fineHttpStatusCodes.contains(statusLine.getStatusCode())){

        }else {
            log.info(httpMethod.getMethod() + " " + httpMethod.getURI() + " strange : " + statusLine);
        }
    }
}
