package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.title.DockingWindowTitleProvider
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.MyDockingWindowTitleProvider
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
class RstaRunnerWithStackTrace2 extends RstaRunnerWithStackTrace3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()

//    public ViewAndPanel runnerView = new ViewAndPanel("Runner", panel);
    public ViewAndPanel stackTraceView = new ViewAndPanel("Thread dump");
    public TextAreaAndView logsView = new TextAreaAndView("Logs  ");
    public TabWindow rightPanel
    public SplitWindow mainPanel4;

    public volatile StringBuffer logs = new StringBuffer()

    public JButton cloneButton = new JButton("Clone")

    public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
    public volatile int modCount = 0

//    Log4j2ThreadAppender log4j2ThreadAppender = new Log4j2ThreadAppender() {
//        @Override
//        void filterPassed(LogEvent loggingEvent) {
//            addLogEvent(loggingEvent)
//        }
//    }

    @Override
    void addLogEvent(LogEvent loggingEvent){
        Date date = new Date(loggingEvent.getTimeMillis());
        modCount++;
        String msg = "${sdf.format(date)} ${loggingEvent.message.getFormattedMessage()}\n";
        logs.append(msg)
    }


    SplitWindow getMainPanel3() {
        return mainPanel4;
    }


    RstaRunnerWithStackTrace2(File file) {
        super(file)
        init4()
    }

    RstaRunnerWithStackTrace2(String text2) {
        super(text2)
        init3()
        init4()
    }

    void doLoayout() {
        rightPanel = new TabWindow()
        rightPanel.addTab(stackTraceView.view)
        rightPanel.addTab(logsView.view)
        doLayoutCreateMainPanel()
    }

    void doLayoutCreateMainPanel() {
        mainPanel4 = new SplitWindow(true, 0.8f, runnerView.view, rightPanel)
    }


    protected void init4() {
        doLoayout()
        logsView.textArea.setEditable(false)
        logsView.textArea.setLineWrap(true)
        cloneButton.addActionListener {
            onCloneActionListener()
        }
        panelButtons.add(cloneButton)
        stackTraceView.panel.add(textAreaStackTrace.scrollPane, BorderLayout.CENTER)
    }

    void onCloneActionListener(){
        TabWindow parentIdwWindowSpecial = IdwUtils.getParentIdwWindowSpecial(runnerView.view.getWindowParent(), TabWindow)
        RstaRunnerWithStackTrace2 newPanel = new RstaRunnerWithStackTrace2(fileWithConfig)
        parentIdwWindowSpecial.addTab(newPanel.getMainPanel3())
        String titleBefore = ' titleBefore '
        DockingWindowTitleProvider titleProvider = getMainPanel3().getWindowProperties().getTitleProvider()
        if(titleProvider!=null){
            titleBefore =  titleProvider.getTitle(getMainPanel3())
        }
        newPanel.getMainPanel3().getWindowProperties().setTitleProvider(new MyDockingWindowTitleProvider(titleBefore))
    }

    @Override
    protected void resetLogs() {
        logs.setLength(0)
        modCount = 0
        analizedModCount = 0
    }

    @Override
    void additionalHilighter() {
        super.additionalHilighter()
        showSTackTrace()
        showLogs()
    }

    volatile int analizedModCount = 0
    public int totalLogSizeTruncated = 0;
    public int maxLogSizeTextLength = 20000;
    public int maxLogSizeTextNewLength = maxLogSizeTextLength/2 as int;

    @Override
    void showLogs() {
//            System.out.println("mod count ${modCount} ${analizedModCount}")
        if (analizedModCount == modCount) {
        } else {
            SwingUtilities.invokeLater {
                analizedModCount = modCount
                String newText = logs.toString();
                int lengthbefore = newText.length();
                if(lengthbefore >maxLogSizeTextLength){
                    newText = truncateText(newText)
                }
//                System.out.println("show text : ${newText}")
//                logsView.textArea.append(new)
                logsView.textArea.setText(newText);
            }

        }
    }

    String truncateText(String newText){
        logs = new StringBuffer()
        totalLogSizeTruncated += maxLogSizeTextNewLength
        logs.append("truncates old logs : ${totalLogSizeTruncated}\n")
        newText= newText.substring(maxLogSizeTextNewLength)
        logs.append(newText)
        return  newText
    }



    @Override
    protected void showStackTraceText(String stackTrace) {
        textAreaStackTrace.textArea.text = stackTrace
    }

}