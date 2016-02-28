package net.sf.jremoterun.utilities.nonjdk.helfyutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.DefaultObjectName
import net.sf.jremoterun.utilities.JrrClassUtils
import one.helfy.JVM

import javax.management.MalformedObjectNameException
import javax.management.ObjectName
import java.util.logging.Logger

@CompileStatic
class HelpfyThreadBean implements DefaultObjectName {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName objectName = new ObjectName("JRemoteRun:type=HelpfyThreadBean")

    void dumpThreadToNative2() {
        dumpThreadToNative()
    }

    Map<Thread, Integer> dumpThreadToNative() {
        return dumpThreadToNativeS()
    }

//    static boolean isGoodNativeThreadId(long nativeThreadId){
//        return 0< nativeThreadId && nativeThreadId < 1000_000
//    }


    static Map<Thread, Integer> dumpThreadToNativeS() {
        Map<Thread, Integer> mapping = ThreadNativeUtils.findThreadIdMapping()
//        Map<Thread, Long> hasMapping = mapping.findAll { isGoodNativeThreadId(it.value)}
//        if (true) {

        String goodMapping2 = mapping.entrySet().collect {
            "${it.value} = ${it.key.getId()} : ${it.key.getName()}"
        }.join('\n')
        log.info "java thread to native thread id mapping :\n${goodMapping2}"
//            log.info "Right java thread to native thread id mapping :\n${goodMapping2}"

//        if (true) {
//            Map<Thread, Long> wrongMapping = mapping.findAll { !isGoodNativeThreadId(it.value) }
//            String badMapping2 = wrongMapping.entrySet().collect {
//                "${it.value} = ${it.key.getId()} : ${it.key.getName()}"
//            }.join('\n')
//            log.info "Wrong java thread to native thread id mapping :\n${badMapping2}"
//        }
        return mapping
    }

    void dumpThreadStack2() {
        one.helfy.ThreadList.main(null)
    }

    void dumpThreadStack(Thread thread) {
        ThreadNativeUtils.dumpThreadStack(thread, System.out)
    }

    void dumpThreadStack(long threadId) {
        Thread thread = Thread.getAllStackTraces().keySet().find { it.id == threadId }
        if (thread == null) {
            throw new Exception("thread not found : ${threadId}")
        }

        ThreadNativeUtils.dumpThreadStack(thread, System.out)
    }

    void dumpVmStructures() {
        new JVM().dump(System.out);
    }


    void f1() {
//        ManagementFactory.getThreadMXBean().getThreadInfo()
    }


    @Override
    ObjectName getDefaultObjectName() throws MalformedObjectNameException {
        return objectName
    }


}
