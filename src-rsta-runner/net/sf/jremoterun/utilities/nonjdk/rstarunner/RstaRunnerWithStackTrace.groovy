package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JSplitPane
import javax.swing.SwingUtilities
import java.awt.*
import java.util.List
import java.util.logging.Logger

@CompileStatic
class RstaRunnerWithStackTrace extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()

    List<String> ignoreClasses = ['java.awt', 'javax.swing', 'groovy.lang.Closure.'];

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, textAreaStackTrace.scrollPane);

    RstaRunnerWithStackTrace(File file) {
        super(checkFileExist2(file));
        init3()
    }

    RstaRunnerWithStackTrace(String text2) {
        super(text2)
        init3()
    }

    private void init3() {

//        panel.add(textAreaStackTrace.scrollPane, BorderLayout.EAST)
        log.info "scroll pane added"
        ignoreClasses.addAll(JrrClassUtils.ignoreClassesForCurrentClass)
        textAreaStackTrace.textArea.setColumns(20)
        splitPane.setDividerLocation(1.0d)
        SwingUtilities.invokeLater {
            splitPane.setDividerLocation(1.0d)
        }
    }

    @Override
    Component getMainPanel() {
        return splitPane
    }

    @Override
    void additionalHilighter() {
        showSTackTrace()
    }

    void showSTackTrace() {
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
                textAreaStackTrace.textArea.text = text3
            }
        }
    }

    @Override
    void codeStopped() {
        SwingUtilities.invokeLater {
            textAreaStackTrace.textArea.text = ""
        }
    }
}