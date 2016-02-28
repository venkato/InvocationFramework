package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.rstacore.GroovyShellGuiRSyntaxTextArea
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.Message
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException

import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.text.BadLocationException
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import java.util.logging.Logger

@CompileStatic
class RstaRunner extends GroovyShellGuiRSyntaxTextArea {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JPanel panelButtons = new JPanel(new FlowLayout())
    JPanel panel = new JPanel(new BorderLayout()) {

        @Override
        public boolean requestFocusInWindow() {
            return requestFocusInWindow2()
        }

    }

    JButton runButton = new JButton(RunnerStatus.Run.name())
    JButton saveToFileButton = new JButton("Save to file")
    Thread thread;
    boolean runInSwingThread = false;
    JTextField progressLable = new JTextField() {

        @Override
        public boolean requestFocusInWindow() {
            return requestFocusInWindow2()
        }

    }


    Component getMainPanel() {
        return panel
    }


    boolean requestFocusInWindow2() {
        return getComponent().requestFocusInWindow()
    }

    ClassLoader classLoader2 = JrrClassUtils.getCurrentClassLoader()

    volatile RstaScriptHelper groovyScriptObject;
//    String generatedClassName2 = "RstaRunner"
//    Binding binding = new Binding();
    volatile boolean stopFlag = false;
//    RunnerStatus runnerStatus = RunnerStatus.Stop

    public Runnable codeFinisedFineListener;
    //File file

    RstaRunner(File file) {
        this(file.text)
        panelButtons.add(saveToFileButton)
        saveToFileButton.addActionListener {
            file.text = getText();
        }
    }

    RstaRunner(String text2) {
        super()
        runButton.setMnemonic(KeyEvent.VK_R);
        panel.add(panelButtons, BorderLayout.NORTH)
        panel.add(getComponent(), BorderLayout.CENTER)
        progressLable.setEditable(false)
        progressLable.setColumns(20)
        panelButtons.add(progressLable)
        panelButtons.add(runButton)
        addLangSupport()
        setText(text2)
        runButton.addActionListener {
            runButtonPressed()
        }
    }

    private void prepareAndRun2() {
        stopFlag = false;
        prepareAndRun()
        progressLable.text = ""
        runButton.enabled = false
        Runnable r = {
            runCode()
        }
        if (runInSwingThread) {
            r.run()
        } else {
            thread = new Thread(r, "RstaRunner")
            thread.start();
        }

    }


    private void runButtonPressed() {
        log.fine("Start time : ${new Date()}");
        requestFocusInWindow2()
        RunnerStatus groovyRunnerState = RunnerStatus.valueOf(runButton.getText());
        switch (groovyRunnerState) {
            case RunnerStatus.Stop:
                runButton.setText(RunnerStatus.Stoping.name());
                runButton.setEnabled(false);
                stopFlag = true;
                break;
            case RunnerStatus.Run:
                prepareAndRun2();
                break;
            case RunnerStatus.Stoping:
                log.info("still stopping");
                break;
        }
    }


    void setStatus(String status) {
        SwingUtilities.invokeLater {
            progressLable.setText(status)
        }
    }

    void runCode() {
        try {
            String text2 = getText().replace("\r\n", "\n").replace("\r", "\n");
            Thread.currentThread().setContextClassLoader(classLoader2);
            GroovyClassLoader classLoader = createGroovyClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            String genClassName = generatedClassName
            if (genClassName == null) {
                genClassName = 'script' + System.currentTimeMillis() +
                        Math.abs(text.hashCode()) + '.groovy'
            }
            generatedClassName = genClassName
            Class parseClass = classLoader.parseClass(text2, genClassName);
//            Thread.currentThread().setContextClassLoader(parseClass.getClassLoader());
            generatedClassName = parseClass.getName()
            codeStarted()
            runGroovyClass(parseClass)
            if (codeFinisedFineListener != null) {
                codeFinisedFineListener.run()
            }
            codeStopped()
        } catch (Throwable e) {
            codeStopped()
            JrrUtilities.showException("", e)
            SwingUtilities.invokeLater { parseException(e) }
        } finally {
            SwingUtilities.invokeLater {
                runButton.enabled = true
                runButton.setText(RunnerStatus.Run.name())
            }
        }
    }

    GroovyClassLoader createGroovyClassLoader() {
        ClassLoader currentClassLoader = classLoader2
        if (classLoader2 instanceof GroovyClassLoader) {
            return (GroovyClassLoader) currentClassLoader;

        }
        return new GroovyClassLoader(classLoader2)
    }

    private void parseException(Throwable e2) throws BadLocationException {
        if (e2 instanceof MultipleCompilationErrorsException) {
            MultipleCompilationErrorsException f1 = (MultipleCompilationErrorsException) e2;
            ErrorCollector errorCollector = f1.getErrorCollector();
            java.util.List<Message> errors = errorCollector.getErrors();
            if (errors.size() > 0) {
                Message message = errors.get(0);
                if (message instanceof SyntaxErrorMessage) {
                    SyntaxErrorMessage syntaxErrorMessage = (SyntaxErrorMessage) message;
                    SyntaxException cause = syntaxErrorMessage.getCause();
                    log.info "${cause.getSourceLocator()} ${generatedClassName}"
                    if (generatedClassName == cause.getSourceLocator()) {
                        int line = cause.getStartLine();
                        log.info "${line}"
                        line += -1;
                        highLightLineAsError(line);
                        return
                    } else {
                        log.info("ignore SyntaxException as another groovy script : " + cause.getSourceLocator());
                    }
                }
            }
        }
        Throwable rootException = JrrUtils.getRootException(e2);
        StackTraceElement[] stackTraces = rootException.getStackTrace();
        final int lineNumber2 = getLineNumberFromStackTrace4(stackTraces);
        if (lineNumber2 != -1) {
            highLightLineAsError(lineNumber2);
        }
    }

    int getLineNumberFromStackTrace4(StackTraceElement[] stackTraces) {
        if(generatedClassName==null ||generatedClassName.length()==0){
            return -1;
        }
        StackTraceElement stackTraceElementF = null;
        for (StackTraceElement stackTraceElement : stackTraces) {
            if (stackTraceElement.getClassName().startsWith(generatedClassName)) {
                stackTraceElementF = stackTraceElement;
                break;
            }
        }
        if (stackTraceElementF != null) {
            int lineNumber = stackTraceElementF.getLineNumber();
            final int lineNumber2 = lineNumber - 1;
            // log.info(lineNumber2);
            return lineNumber2;
        }
        return -1;
    }

    private Object runGroovyClass(Class scriptClass) throws Exception {
        groovyScriptObject = scriptClass.newInstance() as RstaScriptHelper;
        groovyScriptObject.runner = this;
        assert groovyScriptObject.runner != null
//        groovyScriptObject.setBinding(binding)
        assert groovyScriptObject.runner != null
        SwingUtilities.invokeLater {
            if (runButton.isEnabled()) {
                log.info("not enabled ${runButton.getText()}")
            } else {
                runButton.setText(RunnerStatus.Stop.name());
                runButton.setEnabled(true)
            }
        }

        groovyScriptObject.run()
        return null;
    }


}