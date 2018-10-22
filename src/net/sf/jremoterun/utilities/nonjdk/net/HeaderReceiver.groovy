package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.Header
import org.apache.http.StatusLine
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URL
import java.util.logging.Logger;

@CompileStatic
public class HeaderReceiver {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    CloseableHttpClient createClient() {
        CloseableHttpClient aDefault = HttpClients.createDefault();
        return aDefault
    }


    void createClientCred(){
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider()
        basicCredentialsProvider.setCredentials(new AuthScope('host',443),new UsernamePasswordCredentials('userName','pwd'));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(basicCredentialsProvider).build();
    }


    Header getHeader(URL url, String headerName) {
        CloseableHttpClient aDefault = createClient();
        HttpGet httpGet = new HttpGet(url.toString());
        CloseableHttpResponse response = aDefault.execute(httpGet);
        try {
            assert response.getStatusLine().getStatusCode() == 200: url
            Header[] headers = response.getAllHeaders()
            Header header = response.getFirstHeader(headerName)
            if (header == null) {
                throw new IllegalStateException("header not found ${headerName}, headers : ${headers.toList().collect { it.getName() }} ${url}")
            }
            return header
        } finally {
            response.close()
        }
    }

}
