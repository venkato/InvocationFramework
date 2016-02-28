package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class JavaShutdowner implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static ClRef javaShutdownerHooks = new ClRef('java.lang.ApplicationShutdownHooks')

    static ClRef javaShutdownerMain = new ClRef('java.lang.Shutdown')

    public static boolean shutdownHookStarted = false;
    public static long defaultWaitTime = 3_000

    public static IdentityHashMap<Thread, Thread> hooks
    public static Runnable[] hooks4


    static void getHooks() {
        hooks = JrrClassUtils.getFieldValue(javaShutdownerHooks.loadClass2(), 'hooks') as IdentityHashMap
        hooks4 = (Runnable[]) JrrClassUtils.getFieldValue(javaShutdownerMain.loadClass2(), 'hooks')

    }

    static void init() {
        getHooks()
        hooks4[1] = new Runnable() {
            @Override
            void run() {
                runHooks()
            }
        }
    }

    @Override
    void run() {
        init()
    }

    static void runHooks() {
        log.info "starting shutdown hooks"
        if (shutdownHookStarted) {
            log.info "shutdownHookStarted already started"
        }else {
            runHooksImpl()
        }
    }

    static void runHooksImpl() {
        shutdownHookStarted = true
//        getHooks()
        List<Thread> hooks2 = new ArrayList<Thread>(hooks.keySet())
        int hooksCount = hooks2.size()
        if (hooksCount == 0) {
            log.debug "no shutdown hooks"
        } else {
            hooks.clear()
            log.debug "shutdown hooks count : ${hooksCount}"
            hooks2.each {
                log.debug "running : ${it} ${JrrThreadUtils.getTargetForThread(it)}"
                it.start()
            }
            log.info "shutdown hooks started"
            hooks2.each {
                it.join(defaultWaitTime)
                log.info "aii ${it}"
            }
            List<Thread> hooksAlive = hooks2.findAll { it.isAlive() }
            if (hooksAlive.size() > 0) {
                log.info "alive shutdown hooks ${hooksAlive.size()}"
                hooksAlive.each {
                    StringBuilder stackTrace = new StringBuilder()
                    for (StackTraceElement el : it.getStackTrace()) {
                        if (stackTrace.length() != 0) {
                            stackTrace.append('\n')
                        }
                        stackTrace.append(el)
                    }
                    log.warn "alive shutdown hook ${it} : \n${stackTrace}"
                }
            } else {
                log.info "all shutdown hook finished fine"
            }
        }
    }


}
