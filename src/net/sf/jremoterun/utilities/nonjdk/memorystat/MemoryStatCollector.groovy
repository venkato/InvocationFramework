package net.sf.jremoterun.utilities.nonjdk.memorystat

import com.sun.management.GcInfo
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import org.junit.Test

import javax.management.ObjectInstance
import javax.management.ObjectName
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryUsage
import java.util.logging.Logger

@CompileStatic
class MemoryStatCollector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Date lastCheckDate ;

    static List<String> oldGens = ['ConcurrentMarkSweep','PS MarkSweep','G1 Old Generation',]
    static List<String> oldGens2 = ['CMS Old Gen','PS Old Gen','G1 Old Gen']


    @Test
     void collectGcStat4() {
        System.gc()
        System.runFinalization()
        System.gc()
        Thread.sleep(1000)
        collectGcStat3()
    }

    static void collectGcStat3() {
        List<GcInfoBean> gcInfoBeans = collectGcStat()
        gcInfoBeans.findAll {it.bean.lastGcInfo.duration>5000 && (lastCheckDate ==null||it.lastRun.after(lastCheckDate))}.each {
            String msg = "long gc duration = ${it.gcDuration} ms for ${it.bean.name} at ${it.lastRun.format('HH:mm:ss')}"
            log.info "${msg}"
        }
        lastCheckDate = new Date()
        GcInfoBean gcInfoBeanLong = gcInfoBeans.find { oldGens.contains(it.bean.name) }
        if(gcInfoBeanLong==null){
            throw new Exception("failed find old GC from ${gcInfoBeans.collect {it.bean.name}}")
        }
        MemoryPoolMXBean oldGen = ManagementFactory.getMemoryPoolMXBeans().find { oldGens2.contains(it.name) }
        if(oldGen==null){
            throw new Exception("failed find old gen from ${ManagementFactory.getMemoryPoolMXBeans().collect{it.name}}")
        }
        float  usedOldGen =oldGen.usage.used/oldGen.usage.max
        log.info "${usedOldGen}"
        log.info "${gcInfoBeanLong.lastRun}"
        if( usedOldGen> 0.7){
            if(gcInfoBeanLong.lastRun.getTime()>System.currentTimeMillis()-3600_000){
                String msg  = "mem used ${usedOldGen} % ${oldGen.usage.used/1000_000} mb after big gc ${gcInfoBeanLong.lastRun}"
            }
        }
        log.info "gc check finished"
    }


    static List<GcInfoBean> collectGcStat() {
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans()
        List<GcInfoBean> gcInfoBeans = beans.collect { convert(it as com.sun.management.GarbageCollectorMXBean) }.findAll{it!=null}
        return gcInfoBeans
    }

    static GcInfoBean convert(com.sun.management.GarbageCollectorMXBean mxBean) {
        GcInfoBean bean = new GcInfoBean();
        GcInfo lastGcInfo = mxBean.getLastGcInfo()
        if (lastGcInfo == null) {
            return null
        }
        bean.bean = mxBean
        bean.lastRun = new Date(lastGcInfo.getEndTime() + ManagementFactory.getRuntimeMXBean().getStartTime())
        Map<String, MemoryUsage> memoryUsageBeforeGc = lastGcInfo.getMemoryUsageBeforeGc()
        Map<String, MemoryUsage> memoryUsageAfterGc = lastGcInfo.getMemoryUsageAfterGc()
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans()
        beans.each {
            MemoryInstanceInfoGG instanceInfo = new MemoryInstanceInfoGG()
            instanceInfo.name = it.name
            instanceInfo.before = memoryUsageBeforeGc.get(it.name)
            instanceInfo.after = memoryUsageAfterGc.get(it.name)
            BigDecimal diffToNowPercent2 =(it.usage.used - instanceInfo.after.used) / it.usage.max
            instanceInfo.diffToNowPercent = diffToNowPercent2.floatValue();
            BigDecimal freePercent2 = (instanceInfo.before.used - instanceInfo.after.used) / it.usage.max
            instanceInfo.freePercent = freePercent2.floatValue();
        }

        return bean

    }


    @Test
    void collectMemoryStat3() {
        collectMemoryStat2()
    }

    static void collectMemoryStat2() {
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans()
        beans.each {
            MemoryUsage usage = it.usage
            log.info "Memory ${it.name} usage : ${usage.used / 1000_000} mb"
        }


    }


    static List<MemoryPoolMXBean> collectMemoryStat(MbeanConnectionCreator connection) {

        Set<ObjectInstance> memoryMbeans = connection.getMBeanServerConnection().queryMBeans(new ObjectName("java.lang:type=MemoryPool,*"), null); ;
        List<ObjectName> mbeans2 = memoryMbeans.collect { it.objectName }
        List<MemoryPoolMXBean> memoryPoolMXBeans = mbeans2.collect {
            MBeanClient.buildMbeanClient(MemoryPoolMXBean, connection, it)
        }
        return memoryPoolMXBeans
    }


    static void dumpMemoryStatus(List<MemoryPoolMXBean> memoryPoolMXBeans) {
        memoryPoolMXBeans.each {
            MemoryUsage usage = it.getUsage()
        }
    }

}
