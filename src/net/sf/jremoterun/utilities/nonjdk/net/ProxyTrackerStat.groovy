package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@CompileStatic
class ProxyTrackerStat implements ProxyTrackerI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String,Date> firstAccess = new ConcurrentHashMap<>()
    public Map<String,Date> lastAccess = new ConcurrentHashMap<>()

    @Override
    void accessRequested(URI uri, boolean proxyUsed) {
        accessRequested(uri.getHost(),proxyUsed)
    }

    @Override
    void accessRequested(String host, boolean proxyUsed) {
        Date date = new Date()
        if(!firstAccess.containsKey(host)){
            firstAccess.put(host,date)
        }
        lastAccess.put(host,date)

    }
}
