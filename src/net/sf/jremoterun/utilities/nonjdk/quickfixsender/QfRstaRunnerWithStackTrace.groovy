package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.View
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import net.sf.jremoterun.utilities.nonjdk.rstacore.RSyntaxTextAreaCodeAssistUndoFix
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunnerWithStackTrace2
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import quickfix.Message
import quickfix.field.MsgType

import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.ScrollPaneConstants
import javax.swing.table.DefaultTableModel
import javax.swing.text.BadLocationException
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.logging.Logger

@CompileStatic
class QfRstaRunnerWithStackTrace extends RstaRunnerWithStackTrace2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Vector columnNames = new Vector(QfColumns.values().toList().collect { it.name() })



    JTextField statusField = new JTextField("Unknown")
    // used ?
    @Deprecated
    JList list = new JList()

    public TextAreaAndView viewMsgToSendView = new TextAreaAndView("View msg  ");


    DefaultTableModel defaultTableModel = new DefaultTableModel()
    JTable tableMsgs = new JTable(defaultTableModel)
    JScrollPane tableMsgsScrollPane = new JScrollPane(tableMsgs)
    View tableMsgsView = new View('messages', null, tableMsgsScrollPane);
    File configDir;
    JComboBox<String> otherConfigInDir
    JButton loadConfigButton = new JButton("Load send msg config")
    SplitWindow splitPaneRight;
    SplitWindow splitPaneQf;
    QfRunnerType runnerType;

    JrrQfDataHolder d = new JrrQfDataHolder(this)


    JrrQfHelper jrrQfHelperBefore;
    JrrQfHelper jrrQfHelper;


    public List<String> technicalMsgTypesList = [
            MsgType.HEARTBEAT, MsgType.TEST_REQUEST, MsgType.RESEND_REQUEST, MsgType.SEQUENCE_RESET,
            MsgType.LOGOUT, MsgType.LOGON,
    ]

    // connect fields begin
    JButton connectButton = new JButton("Connect")
    File configConnectionDir;
    JComboBox<String> otherConfigConnectInDir

    JButton saveConnectConfigButton = new JButton("Save to file")
    JButton loadConnectionConfigButton = new JButton("Load config")


    public JPanel connectButtonsPanel = new JPanel(new FlowLayout())


    public static String groovySuffix = '.groovy'

    File fileWithConnectionConfig;

    RSyntaxTextAreaCodeAssistUndoFix connectRunnerTextArea = new RSyntaxTextAreaCodeAssistUndoFix() {
        @Override
        void appendFoldingMenu2(JPopupMenu popupMenu) {
            appendFoldingMenu3(popupMenu);
        }
    };


    public JPanel connectPanel = new JPanel(new BorderLayout()) {

        @Override
        public boolean requestFocusInWindow() {
            return connectRunnerTextArea.requestFocusInWindow()
        }

    }

    public ViewAndPanel viewConnectView = new ViewAndPanel('Connect', connectPanel);

    // connect fields end

    QfRstaRunnerWithStackTrace(File file, File connectFile) {
        super(file)

        connectRunnerTextArea.scrollPane = new RTextScrollPane(connectRunnerTextArea, true);
        connectRunnerTextArea.setTabSize(2);
        connectRunnerTextArea.setCodeFoldingEnabled(true)
        connectRunnerTextArea.addLangSupport()
        connectRunnerTextArea.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        connectRunnerTextArea.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        connectRunnerTextArea.scrollPane.setIconRowHeaderEnabled(true);
        fileWithConnectionConfig = connectFile;
        this.configDir = file.getParentFile()
        if (configDir != null) {
            assert configDir.exists();
            List<File> files = configDir.listFiles().toList().findAll { it.getName().endsWith(groovySuffix) }
            List<String> names = files.collect { it.getName().replace(groovySuffix, '') }
            String[] namesArray = names.toArray(new String[0])
            otherConfigInDir = new JComboBox<String>(namesArray);
            otherConfigInDir.setSelectedItem(file.getName().replace(groovySuffix, ''))
            loadConfigButton.addActionListener {
                try {
                    loadConfig()
                } catch (Throwable e2) {
                    log.info("failed load config ", e2)
                    showException(e2)
                }

            }
            panelButtons.add(otherConfigInDir)
            panelButtons.add(loadConfigButton)
        }
        if (true) {
            connectPanel.add(connectButtonsPanel, BorderLayout.NORTH)
            connectPanel.add(connectRunnerTextArea.scrollPane, BorderLayout.CENTER)
            configConnectionDir = connectFile.getParentFile();
            assert configConnectionDir.exists();
            List<File> files = configConnectionDir.listFiles().toList().findAll { it.getName().endsWith(groovySuffix) }
            List<String> names = files.collect { it.getName().replace(groovySuffix, '') }
            String[] namesArray = names.toArray(new String[0])
            otherConfigConnectInDir = new JComboBox<String>(namesArray);
            otherConfigConnectInDir.setSelectedItem(connectFile.getName().replace(groovySuffix, ''))
            loadConnectionConfigButton.addActionListener {
                try {
                    loadConnectionConfig()
                } catch (Throwable e2) {
                    log.info("failed load connection config ", e2)
                    showException(e2)
                }

            }
            saveConnectConfigButton.addActionListener {
                fileWithConnectionConfig.text = connectRunnerTextArea.getTextNormalized();
            }
            connectRunnerTextArea.setText(fileWithConnectionConfig.text)
            connectButtonsPanel.add(connectButton)
            connectButtonsPanel.add(otherConfigConnectInDir)
            connectButtonsPanel.add(loadConnectionConfigButton)
            connectButtonsPanel.add(saveConnectConfigButton)

        }
        defaultTableModel.setColumnIdentifiers(columnNames)
        connectButton.addActionListener {
            runnerType = QfRunnerType.connect;
            prepareAndRun2(connectButton.getText())
        }
//        sendMsgButton.addActionListener {
//            prepareAndRun2("sendMsg")
//        }
//        customButton.addActionListener {
//            prepareAndRun2("custom")
//        }


//        panelButtons.add(sendMsgButton)
//        panelButtons.add(customButton)
        statusField.setEditable(false)
        panelButtons.add(statusField)
//        msgToSendView.getTextArea().setText("|9=1|35=P|87=0|70=1")
        viewMsgToSendView.textArea.setLineWrap(true)

//        rightPanel.addTab(msgToSendView.view)
        tableMsgs.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                try {
                    showMsg(tableMsgs.getSelectedRow())
                } catch (Throwable e2) {
                    log.info("failed show selected msg ", e2)
                    showException(e2)
                }
            }
        });
        viewMsgToSendView.getTextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML)
        doLayoutQf()
    }

    @Override
    void onCloneActionListener() {
        TabWindow parentIdwWindowSpecial = IdwUtils.getParentIdwWindowSpecial(runnerView.view.getWindowParent(), TabWindow)
        RstaRunnerWithStackTrace2 newPanel = new QfRstaRunnerWithStackTrace(fileWithConfig, fileWithConnectionConfig)
        parentIdwWindowSpecial.addTab(newPanel.getMainPanel3())
    }

    void doLayoutQf() {
        TabWindow tabWindow2 = new TabWindow()
        tabWindow2.addTab(viewMsgToSendView.view)
        tabWindow2.addTab(viewConnectView.view)
        splitPaneRight = new SplitWindow(false, 0.5f, rightPanel, tabWindow2)
        splitPaneQf = new SplitWindow(false, 0.8f, runnerView.view, tableMsgsView)
        mainPanel4 = new SplitWindow(true, 0.8f, splitPaneQf, splitPaneRight)
        IdwUtils.setTitle(mainPanel4, 'QuickFix sender')
    }

    void loadConfig() {
        String ss = otherConfigInDir.getSelectedItem()
        File f = configDir.child(ss + groovySuffix)
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "File not exit : ${f}")
        } else {
            stopCurrent()
            fileWithConfig = f
            textAreaRunner.textArea.setText(f.text)
        }
    }

    void loadConnectionConfig() {
        String ss = otherConfigConnectInDir.getSelectedItem()
        File f = configConnectionDir.child(ss + groovySuffix)
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "File connection not exit : ${f}")
        } else {
            stopCurrent()
            fileWithConnectionConfig = f
            connectRunnerTextArea.setText(f.text)
        }
    }


    void stopCurrent() {
        d.modificationsCount++;
        if (d.connector != null) {
            d.connector.stop(true)
        }
    }


    void showMsg(int activeColumn) {
        Vector row = defaultTableModel.getDataVector().get(activeColumn)
        int i = columnNames.indexOf(QfColumns.msg.name())
        String msg = row.get(i);
        showMsgAsXml(msg)
    }


    void showMsgAsXml(String msg) {
        msg = msg.replace(d.humanSep, d.fixMsgNativeSep)
        Message message = new Message(msg, jrrQfHelper.getAppDd())
        String asXml = message.toXML(jrrQfHelper.appDd)
        asXml = removeXmlUseless(asXml)
        viewMsgToSendView.getTextArea().setText(asXml)
    }


    static String removeXmlUseless(String asXml) {

        asXml = asXml.replace('<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>', '')
        asXml = asXml.replace('<?xml version="1.0" encoding="ISO-8859-1"?>', '')
        asXml = asXml.replace('<![CDATA[', '')
        asXml = asXml.replace(']]>', '')
        return asXml
    }

    @Override
    void enableButtons() {
        super.enableButtons()
        connectButton.setEnabled(true)

//        sendMsgButton.setEnabled(true)
//        customButton.setEnabled(true)
    }

    @Override
    void disableButtons() {
        super.disableButtons()
        connectButton.setEnabled(false)
//        sendMsgButton.setEnabled(false)
//        customButton.setEnabled(false)

    }

    @Override
    public void prepareAndRun() {
        textArea.removeAllLineHighlights();
        connectRunnerTextArea.removeAllLineHighlights()
    }

    @Override
    public void highLightLineAsError(int line) throws BadLocationException {
        if (runnerType == QfRunnerType.connect) {
            connectRunnerTextArea.addLineHighlight(line, Color.PINK);
        } else {
            //textArea.addLineHighlight(line, Color.PINK);
            super.highLightLineAsError(line);
        }
    }

    @Override
    String getTextToRun() {
        if (runnerType == QfRunnerType.connect) {
            return connectRunnerTextArea.getTextNormalized()
        }
        return super.getTextToRun()
    }

    @Override
    void codeStopped() {
        super.codeStopped()
        connectRunnerTextArea.removeAllLineHighlights()
        runnerType = null
    }

    @Override
    protected void highLightCurrentExecutingLine(int line) throws BadLocationException {
//        textArea.removeAllLineHighlights();
        connectRunnerTextArea.removeAllLineHighlights()
        if (runnerType == QfRunnerType.connect) {
            connectRunnerTextArea.addLineHighlight(line, Color.cyan);
        } else {
            //textArea.addLineHighlight(line, Color.cyan);
            super.highLightCurrentExecutingLine(line)
        }
    }


    @Override
    void doLayoutCreateMainPanel() {

    }


    void showException(Throwable e) {
        e = JrrUtils.getRootException(e)
        String excS = JrrUtils.exceptionToString(e)
        viewMsgToSendView.getTextArea().setText(excS)
        IdwUtils.setVisible(viewMsgToSendView.view)
    }

    void addMessageToView(Message msg, boolean isOutMsg) {
        try {
            if (jrrQfHelper.isNeedAddMsgToTable(msg, isOutMsg)) {
                Vector row = new Vector()
                row.add sdf.format(new Date())
                String inOut = isOutMsg ? 'out' : 'in'
                row.add inOut
                row.add getMsgMarker(msg)
                row.add msg.toString().replace(d.fixMsgNativeSep, d.humanSep);
                defaultTableModel.addRow(row)
            }
        } catch (Throwable e) {
            log.info("faild add msg ${isOutMsg} : ${msg} ", e)
            showException(e)
        }
    }

    Character getMsgMarker(Message msg) {
        Message.Header header = msg.getHeader()
        if (header == null) {
            return null
        }
        if (!header.isSetField(MsgType.FIELD)) {
            return null
        }
        String msgType = header.getString(MsgType.FIELD)
        Character res
        if (technicalMsgTypesList.contains(msgType)) {
            res = 'T';
        } else {
            res = 'U'
        }

        return res;
    }

//    @Override
//    Object runGroovyClass(Class scriptClass, Object param) throws Exception {
////        if (param == null) {
////            throw new IllegalStateException("Param is null. wrong button ?")
////        }
//        super.runGroovyClass(scriptClass, param)
//        String methodName = param;
//        jrrQfHelperBefore = jrrQfHelper;
//        jrrQfHelper = groovyScriptObject as JrrQfHelper
//        jrrQfHelper.t = this
//        jrrQfHelper.d = d
//        //JrrClassUtils.invokeJavaMethod(groovyScriptObject, methodName)
//        jrrQfHelper.run()
//        return null
//    }

    @Override
    void preRunAfterScriptLoaded() {
        super.preRunAfterScriptLoaded()
        jrrQfHelperBefore = jrrQfHelper;
        jrrQfHelper = groovyScriptObject as JrrQfHelper
        jrrQfHelper.t = this
        jrrQfHelper.d = d
    }
// ----------------------------------------


}
