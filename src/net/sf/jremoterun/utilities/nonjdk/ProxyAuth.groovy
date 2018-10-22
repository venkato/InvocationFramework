package net.sf.jremoterun.utilities.nonjdk;

import groovy.transform.CompileStatic;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

// see : org.apache.ivy.util.url.IvyAuthenticator
@CompileStatic
public class ProxyAuth extends Authenticator {
    private PasswordAuthentication auth;

    public ProxyAuth(String user, String password) {
        char[] password2
        if(password==null){
            password2 = new char[0]
        }else{
            password2 = password.toCharArray()
        }
        auth = new PasswordAuthentication(user, password2);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return auth;
    }
}