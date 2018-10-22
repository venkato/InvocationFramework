package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ProxyTrackerI {


    void accessRequested(URI uri,boolean proxyUsed);


    void accessRequested(String host,boolean proxyUsed);

}
