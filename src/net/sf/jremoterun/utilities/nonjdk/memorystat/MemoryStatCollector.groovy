package net.sf.jremoterun.utilities.nonjdk.memorystat

import com.sun.management.GcInfo
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.DefaultObjectName
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator

import javax.management.MalformedObjectNameException
import javax.management.ObjectInstance
import javax.management.ObjectName
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryUsage
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
abstract class MemoryStatCollector implements DefaultObjectName {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName objectName = new ObjectName('jrr:type=memoryUsage')

    public static List<String> oldGens = ['ConcurrentMarkSweep', 'PS MarkSweep', 'G1 Old Generation',]
    public static List<String> oldGens2 = ['CMS Old Gen', 'PS Old Gen', 'G1 Old Gen']
    public Date lastCheckDate;

    public float maxUsedOldGenPercent = 70f
    public float maxAllowedGcDurationMs = 5000
    public long ignoreLongGcHappenedOlderThen = 3600_000
    public static long oneMegaByte = 1000_000

    abstract void onMsg(String msg);

    @Override
    ObjectName getDefaultObjectName() throws MalformedObjectNameException {
        return objectName
    }

    void printBadGc(GcInfoBean gcInfoBean) {
        SimpleDateFormat sdf= new SimpleDateFormat('HH:mm:ss')
        String msg = "long gc duration = ${gcInfoBean.gcDuration} ms for ${gcInfoBean.bean.name} at ${sdf.format(gcInfoBean.lastRun)}"
        onMsg(msg)
    }

    boolean isBadGc(GcInfoBean it) {
        if (it.bean.getLastGcInfo() == null) {
            return false
        }
        return it.bean.lastGcInfo.duration > maxAllowedGcDurationMs && (lastCheckDate == null || it.lastRun.after(lastCheckDate))
    }

    GcInfoBean findOldGcInfoAndPrintStat() {
        List<GcInfoBean> gcInfoBeans = getGcStat()
        List<GcInfoBean> badGc = gcInfoBeans.findAll { isBadGc(it) };
        badGc.each { printBadGc(it) }
        GcInfoBean gcInfoBeanLong = findOldGcInfo(gcInfoBeans)
        return gcInfoBeanLong;
    }

    GcInfoBean findOldGcInfo(List<GcInfoBean> gcInfoBeans) {
        GcInfoBean gcInfoBeanLong = gcInfoBeans.find { oldGens.contains(it.bean.name) }
        if (gcInfoBeanLong == null) {
            throw new Exception("failed find old GC from ${gcInfoBeans.collect { it.bean.name }}")
        }
        return gcInfoBeanLong
    }

    MemoryInfoBean  getOldGenMemoryInfo() {
        MemoryPoolMXBean oldGen = ManagementFactory.getMemoryPoolMXBeans().find { oldGens2.contains(it.name) }
        if (oldGen == null) {
            throw new Exception("failed find old gen from ${ManagementFactory.getMemoryPoolMXBeans().collect { it.name }}")
        }
        return convertToHuman(oldGen)
    }

    void collectGcStat3() {
        lastCheckDate = new Date()
        GcInfoBean gcInfoBeanLong = findOldGcInfoAndPrintStat()
        MemoryInfoBean oldGen = getOldGenMemoryInfo()
//        float usedOldGen = oldGen.usage.used / oldGen.usage.max
//        log.info "${usedOldGen}"
//        log.info "${gcInfoBeanLong.lastRun}"
        if (oldGen.usedPercent > maxUsedOldGenPercent) {
            if (gcInfoBeanLong.bean.getLastGcInfo() == null) {
                log.info "high memory usage : ${oldGen.usedPercent} %, gc in old space was not run before"
            } else {
                if (gcInfoBeanLong.lastRun.getTime() > System.currentTimeMillis() - ignoreLongGcHappenedOlderThen) {
                    String msg = "mem used ${oldGen.usedPercent} % ${oldGen.used / oneMegaByte} mb after big gc ${gcInfoBeanLong.lastRun}"
                    onMsg(msg)
                }
            }
        }
        log.info "gc check finished"
    }

    List<MemoryInfoBean> getMemoryStat() {
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans()
        List<MemoryInfoBean> res = beans.collect { convertToHuman(it) }
        return res
    }

    MemoryInfoBean convertToHuman(MemoryPoolMXBean m) {
        MemoryInfoBean memoryInfoBean = new MemoryInfoBean()
        memoryInfoBean.nativeBean = m
        MemoryUsage peakUsage = m.getPeakUsage()
        MemoryUsage collectionUsage = m.getCollectionUsage()
        MemoryUsage usage = m.getUsage()
        memoryInfoBean.peek = m.getPeakUsage().getUsed()
        memoryInfoBean.used = usage.getUsed()
        memoryInfoBean.max = usage.getMax()
        memoryInfoBean.name = m.getName()
        memoryInfoBean.usedPercent = (100f*memoryInfoBean.used /memoryInfoBean.max) as float
        return memoryInfoBean;

    }


    List<GcInfoBean> getGcStat() {
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans()
        List<GcInfoBean> gcInfoBeans = beans.collect { convert(it as com.sun.management.GarbageCollectorMXBean) };

        gcInfoBeans = gcInfoBeans.findAll { it != null }
        return gcInfoBeans
    }

    GcInfoBean convert(com.sun.management.GarbageCollectorMXBean mxBean) {
        GcInfoBean bean = new GcInfoBean();
        GcInfo lastGcInfo = mxBean.getLastGcInfo()
        bean.gcName = mxBean.getName()
        bean.bean = mxBean
        if (lastGcInfo == null) {
            return bean
        }
        bean.lastRun = new Date(lastGcInfo.getEndTime() + ManagementFactory.getRuntimeMXBean().getStartTime())
        Map<String, MemoryUsage> memoryUsageBeforeGc = lastGcInfo.getMemoryUsageBeforeGc()
        Map<String, MemoryUsage> memoryUsageAfterGc = lastGcInfo.getMemoryUsageAfterGc()
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans()
        beans.each {
            MemoryInstanceInfoGG instanceInfo = new MemoryInstanceInfoGG()
            instanceInfo.name = it.name
            instanceInfo.before = memoryUsageBeforeGc.get(it.name)
            instanceInfo.after = memoryUsageAfterGc.get(it.name)
            BigDecimal diffToNowPercent2 = (it.usage.used - instanceInfo.after.used) / it.usage.max
            instanceInfo.diffToNowPercent = diffToNowPercent2.floatValue();
            BigDecimal freePercent2 = (instanceInfo.before.used - instanceInfo.after.used) / it.usage.max
            instanceInfo.freePercent = freePercent2.floatValue();
        }

        return bean

    }


    void collectMemoryStat2() {
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans()
        beans.each {
            MemoryUsage usage = it.usage
            log.info "Memory ${it.name} usage : ${usage.used / oneMegaByte} mb"
        }


    }


    List<MemoryPoolMXBean> collectMemoryStat(MbeanConnectionCreator connection) {

        Set<ObjectInstance> memoryMbeans = connection.getMBeanServerConnection().queryMBeans(new ObjectName("java.lang:type=MemoryPool,*"), null); ;
        List<ObjectName> mbeans2 = memoryMbeans.collect { it.objectName }
        List<MemoryPoolMXBean> memoryPoolMXBeans = mbeans2.collect {
            MBeanClient.buildMbeanClient(MemoryPoolMXBean, connection, it)
        }
        return memoryPoolMXBeans
    }


    void dumpMemoryStatus(List<MemoryPoolMXBean> memoryPoolMXBeans) {
        memoryPoolMXBeans.each {
            MemoryUsage usage = it.getUsage()
        }
    }

}
