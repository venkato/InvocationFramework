package net.sf.jremoterun.utilities.nonjdk.helfyutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import one.helfy.JVM
import one.helfy.VMThread
import one.helfy.vmstruct.Frame
import one.helfy.vmstruct.JavaThread

import java.util.logging.Logger

@CompileStatic
class ThreadNativeUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static Map<Thread, Integer> findThreadIdMapping() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet()
        Map<Thread, Integer> threadToNativeMapping = threads.collectEntries { [(it): findNativeThreadId(it)] }

        return threadToNativeMapping
    }

    static int findNativeThreadId(Thread thread) {
        JVM jvm = new JVM()
        long off_osthread = jvm.type("JavaThread").offset('_osthread')
        long off_thread_id = jvm.type("OSThread").offset('_thread_id')
        long vmThread = VMThread.of(thread)
        long osThread = jvm.getAddress(vmThread + off_osthread)
        int nateiveThreadId = jvm.getInt(osThread + off_thread_id);
        return nateiveThreadId
    }


    static void dumpThreadStack(Thread thread, PrintStream outputStream) {
        long vmThread = VMThread.of(thread);
        for (Frame f = JavaThread.topFrame(vmThread); f != null; f = f.sender()) {
            f.dump(outputStream);
        }
    }
}
