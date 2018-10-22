package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.management.MBeanServer
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean;
import java.util.logging.Logger;

@CompileStatic
class JavaStartTime {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Date getStartTime(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        return new Date(runtimeMXBean.getStartTime())
    }




}
