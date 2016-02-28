package net.sf.jremoterun.utilities.nonjdk.memorystat;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.GarbageCollectorMXBean;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GcInfoBean {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String gcName;
    public Date lastRun;
    public long gcId;
    public long gcDuration;
    public com.sun.management.GarbageCollectorMXBean bean;
    List<MemoryInstanceInfoGG> infoGG = []


}
