package net.sf.jremoterun.utilities.nonjdk.log.threadfilter

import groovy.transform.CompileStatic
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender

@CompileStatic
public abstract class Log4j2ThreadAppender extends AbstractAppender {


    public volatile Thread loggingThread;

    public HashSet<String> loggingLogger = new HashSet<String>();
    public HashSet<String> ignoreLoggers = new HashSet<String>();

    public volatile boolean translateAllLoggers = false;
    public volatile boolean translateAllLoggersForExecuterThread = false;

    public Log4j2ThreadAppender() throws Exception {
        super("RemoteAppender", null, null);
        loggingThread = Thread.currentThread();
    }

    public void addClassLoggingIgnore(Class clazz) {
        ignoreLoggers.add(clazz.getName())
    }

    public void addClassToLog4jTranslate(Class clazz) {
        loggingLogger.add(clazz.getName());
    }

    public boolean isPassEvent(LogEvent loggingEvent) {
        boolean passed = false;
        if (!passed && translateAllLoggers) {
            passed = true;
        }
        if (!passed && Thread.currentThread() == loggingThread && translateAllLoggersForExecuterThread) {
            passed = true
        }
        if (!passed && loggingLogger.contains(loggingEvent.getLoggerName())) {
            passed = true
        }
        if (passed) {
            if (ignoreLoggers.contains(loggingEvent.getLoggerName())) {
                passed = false
            }
        }
//        System.out.println("event passed ${passed} , msg = ${loggingEvent.message}")
        return passed
    }


    @Override
    void append(LogEvent loggingEvent) {
        boolean isLog = isPassEvent(loggingEvent);
//        System.out.println("filter passed ${isLog}");
        if (isLog) {
            filterPassed(loggingEvent);
        } else {
//            Thread.dumpStack()
        }
    }

    public abstract void filterPassed(LogEvent loggingEvent);

    @Override
    boolean isStarted() {
        return true
    }
}
