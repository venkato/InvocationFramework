package net.sf.jremoterun.utilities.nonjdk.memorystat;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.GarbageCollectorMXBean;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GcInfoBean {
    // implements Serializable
    // can't be ser due to GarbageCollectorMXBean not ser

    public String gcName;
    public Date lastRun;
    public long gcDuration;
    public com.sun.management.GarbageCollectorMXBean bean;

    @Override
    String toString() {
        return gcName
    }
}
