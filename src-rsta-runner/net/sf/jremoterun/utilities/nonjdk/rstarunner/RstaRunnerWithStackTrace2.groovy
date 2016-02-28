package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import net.sf.jremoterun.utilities.nonjdk.log.threadfilter.Log4j2ThreadAppender
import org.apache.logging.log4j.core.LogEvent

import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat
import java.util.List
import java.util.logging.Logger

@CompileStatic
class RstaRunnerWithStackTrace2 extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()

    List<String> ignoreClasses = ['java.awt', 'javax.swing', 'groovy.lang.Closure.'];

    public ViewAndPanel runnerView = new ViewAndPanel("Runner", panel);
    public ViewAndPanel stackTraceView = new ViewAndPanel("Thread dump");
    public TextAreaAndView logsView = new TextAreaAndView("Logs  ");
    public TabWindow rightPanel = new TabWindow()
    public SplitWindow mainPanel3 = new SplitWindow(true, 0.8f, runnerView.view, rightPanel)

    public StringBuilder logs = new StringBuilder()

    Log4j2ThreadAppender log4j2ThreadAppender = new Log4j2ThreadAppender() {
        @Override
        void filterPassed(LogEvent loggingEvent) {
            Date date = new Date(loggingEvent.getTimeMillis());
            modCount++;
            String msg = "${sdf.format(date)} ${loggingEvent.message.getFormattedMessage()}\n";
//                System.out.println("got new msg : ${msg}")
            logs.append(msg)
        }
    }
    ;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
    volatile int modCount = 0

    RstaRunnerWithStackTrace2(File file) {
        super(file)
        init3()
    }

    RstaRunnerWithStackTrace2(String text2) {
        super(text2)
        init3()
    }

    private void init3() {
        rightPanel.addTab(stackTraceView.view)
        rightPanel.addTab(logsView.view)
        logsView.textArea.setEditable(false)
        logsView.textArea.setLineWrap(true)
        stackTraceView.panel.add(textAreaStackTrace.scrollPane, BorderLayout.CENTER)
//        panel.add(textAreaStackTrace.scrollPane, BorderLayout.EAST)
        log.info "scroll pane added"
        ignoreClasses.addAll(JrrClassUtils.ignoreClassesForCurrentClass)
        Log4j2Utils.rootLogger.addAppender(log4j2ThreadAppender)
        log4j2ThreadAppender.translateAllLoggersForExecuterThread = true
    }

    @Override
    Component getMainPanel() {
        throw new IllegalStateException()
    }

    @Override
    void codeStarted() {
        logs.setLength(0)
        modCount = 0
        analizedModCount = 0
        log4j2ThreadAppender.loggingThread = Thread.currentThread()
        // log4j2ThreadAppender.loggingThread = Thread.currentThread()
        super.codeStarted()
    }


    @Override
    void additionalHilighter() {
        super.additionalHilighter()
        showSTackTrace()
        showLogs()
    }

    volatile int analizedModCount = 0

    void showLogs() {
//            System.out.println("mod count ${modCount} ${analizedModCount}")
        if (analizedModCount == modCount) {
        }else{
            SwingUtilities.invokeLater {
                analizedModCount = modCount
                String newText = logs.toString();
//                System.out.println("show text : ${newText}")
//                logsView.textArea.append(new)
                logsView.textArea.setText(newText);
            }

        }
    }


    void showSTackTrace() {
        Thread codeThread2 = codeThread
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
                textAreaStackTrace.textArea.text = text3
            }
        }
    }

    @Override
    void codeStopped() {
        super.codeStopped()
        SwingUtilities.invokeLater {
            textAreaStackTrace.textArea.text = ""
        }
        // Log4j2Utils.rootLogger.removeAppender(log4j2ThreadAppender)
        showLogs()
    }
}