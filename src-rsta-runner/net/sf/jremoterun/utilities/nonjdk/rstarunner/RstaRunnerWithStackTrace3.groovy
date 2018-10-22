package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import net.sf.jremoterun.utilities.nonjdk.log.threadfilter.Log4j2ThreadAppender
import org.apache.logging.log4j.core.LogEvent

import javax.swing.*
import java.awt.*
import java.util.List
import java.util.logging.Logger

@CompileStatic
abstract class RstaRunnerWithStackTrace3 extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()

    List<String> ignoreClasses = ['java.awt', 'javax.swing', 'groovy.lang.Closure.'];

    public ViewAndPanel runnerView = new ViewAndPanel("Runner", panel);
    //public ViewAndPanel stackTraceView = new ViewAndPanel("Thread dump");
    //public TextAreaAndView logsView = new TextAreaAndView("Logs  ");
//    public TabWindow rightPanel
//    public SplitWindow mainPanel4;

//    public volatile StringBuffer logs = new StringBuffer()

//    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
//    volatile int modCount = 0

    Log4j2ThreadAppender log4j2ThreadAppender = new Log4j2ThreadAppender() {
        @Override
        void filterPassed(LogEvent loggingEvent) {
            addLogEvent(loggingEvent)
        }
    }

    abstract void addLogEvent(LogEvent loggingEvent)


//    SplitWindow getMainPanel3() {
//        return mainPanel4;
//    }


    RstaRunnerWithStackTrace3(File file) {
        super(file)
        init3()
    }

    RstaRunnerWithStackTrace3(String text2) {
        super(text2)
        init3()
    }

    //abstract void doLoayout()
//        rightPanel = new TabWindow()
//        rightPanel.addTab(stackTraceView.view)
//        rightPanel.addTab(logsView.view)
//        doLayoutCreateMainPanel()
//    }

//    void doLayoutCreateMainPanel() {
//        mainPanel4 = new SplitWindow(true, 0.8f, runnerView.view, rightPanel)
//    }

    protected void init3() {
        //doLoayout()
//        logsView.textArea.setEditable(false)
//        logsView.textArea.setLineWrap(true)
//        stackTraceView.panel.add(textAreaStackTrace.scrollPane, BorderLayout.CENTER)
//        panel.add(textAreaStackTrace.scrollPane, BorderLayout.EAST)
        log.info "scroll pane added"
        ignoreClasses.addAll(JrrClassUtils.ignoreClassesForCurrentClass)
        if (Log4j2Utils.rootLogger == null) {
            log.info "root logger is null"
        } else {
            String appenderName = buildLogAppenderName()
            log4j2ThreadAppender.setAppenderName(appenderName)
            Log4j2Utils.rootLogger.addAppender(log4j2ThreadAppender)
        }
        log4j2ThreadAppender.translateAllLoggersForExecuterThread = true
    }

    String buildLogAppenderName() {
        String name2 = "RstaRunner-${getClass().getSimpleName()}-"
        String name3 = name2 + new Random().nextInt()
        Set<String> set2 = Log4j2Utils.rootLogger.getAppenders().keySet();
        while (set2.contains(name2)) {
            name3 = name2 + new Random().nextInt()
        }
        return name3
    }

//    @Override
//    Component getMainPanel() {
//        throw new IllegalStateException()
//    }

    protected abstract void resetLogs()

    @Override
    void codeStarted() {
        super.codeStarted()
        resetLogs()
//        modCount = 0
//        analizedModCount = 0
        log4j2ThreadAppender.loggingThread = Thread.currentThread()
        super.codeStarted()
    }


    @Override
    void additionalHilighter() {
        super.additionalHilighter()
        showSTackTrace()
        showLogs()
    }

    protected void showSTackTrace() {
        Thread codeThread2 = textAreaRunner.codeThread
        if (codeThread2 == null) {
            log.info("thread value is null");
        } else {
            List<StackTraceElement> stackTraces = codeThread2.getStackTrace().toList();
            stackTraces = stackTraces.findAll { StackTraceElement el ->
                String className = el.getClassName()
                return ignoreClasses.find { className.startsWith(it) } == null
            }
            String text3 = stackTraces.join('\n');
            SwingUtilities.invokeLater {
                showStackTraceText(text3)
            }
        }
    }

    protected abstract void showStackTraceText(String stackTrace);

    protected abstract void showLogs();

    // t:dual
    @Override
    void codeStopped() {
        super.codeStopped()
        SwingUtilities.invokeLater {
            showStackTraceText('')
        }
        // Log4j2Utils.rootLogger.removeAppender(log4j2ThreadAppender)
        showLogs()
    }
}