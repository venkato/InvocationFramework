package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class GetMyHostName {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String getMyHostname(){
        InetAddress localHost = InetAddress.getLocalHost();
        return localHost.getHostName();
    }

}
