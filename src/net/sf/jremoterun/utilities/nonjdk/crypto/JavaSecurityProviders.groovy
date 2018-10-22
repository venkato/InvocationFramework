package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef

import java.security.Security;
import java.security.Provider;


@CompileStatic
enum JavaSecurityProviders {

    SunJCE(null),BC(new ClRef('org.bouncycastle.jce.provider.BouncyCastleProvider')),
    ;

    public ClRef className;

    JavaSecurityProviders(ClRef className) {
        this.className = className
    }

    Provider loadProvider(){
        Security.getProvider(name());
    }


}