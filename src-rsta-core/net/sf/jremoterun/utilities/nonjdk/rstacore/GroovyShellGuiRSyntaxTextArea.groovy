package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.rsta.ac.LanguageSupportFactory
import org.fife.rsta.ac.java.JavaLanguageSupport
import org.fife.rsta.ac.java.custom.RSyntaxTextAreaCodeAssist
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import javax.swing.text.BadLocationException
import javax.swing.text.Caret
import java.awt.Color
import java.awt.Component
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public abstract class GroovyShellGuiRSyntaxTextArea {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String generatedClassName;

    private ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();

    private long editsTry = 0;

    final RSyntaxTextAreaCodeAssist  textArea;

    final RTextScrollPane scrollPane;

    public GroovyShellGuiRSyntaxTextArea() {
        textArea = createTextArea();
        scrollPane = new RTextScrollPane(textArea, true);
        setEditable(true);
        textArea.setTabSize(2);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setIconRowHeaderEnabled(true);
        scrollPane.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {
                textArea.requestFocusInWindow();
            }
        });
        codeExecutionHilighter.start();
        textArea.setCodeFoldingEnabled(true);
    }

    protected RSyntaxTextAreaCodeAssist createTextArea() {
        return new RSyntaxTextAreaCodeAssistWithCustMenu() {
            @Override
            void appendFoldingMenu2(JPopupMenu popupMenu) {
                appendFoldingMenu3(popupMenu);
            }
        };
    }

    void appendFoldingMenu3(JPopupMenu popupMenu){
        for (JMenuItem menuItem : menuItems) {
            popupMenu.add(menuItem);
        }

    }

    public void setText(String text) {
        textArea.setText(text);
        textArea.setHyperlinksEnabled(true);
        scrollPane.setLineNumbersEnabled(true);
        textArea.setHighlightCurrentLine(false);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.select(0, 0);
        if (!isEditable()) {
            editsTry++;
            if (editsTry > 5) {
                // discarding for avoiding memory leak
                textArea.discardAllEdits();
                editsTry = 0;
            }
        }
    }

//    abstract String getScriptText() throws IOException

    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();

    }

    public void requestFocus() {
        textArea.requestFocus();
    }

    public String getText() {
        String text = textArea.getText();
        text = text.replace("\r\n", "\n").replace("\r", "\n");
        return text;
    }

    public String getSelectedText() {
        String selectedText = textArea.getSelectedText();
        if (selectedText == null) {
            return null;
        }
        selectedText = selectedText.replace("\r\n", "\n").replace("\r", "\n");
        return selectedText;
    }

    public void addLangSupport() throws Exception {
        RstaLangSupportStatic langSupport = RstaLangSupportStatic.langSupport;
        langSupport.init();
//        RSyntaxTextArea textArea = this.getTextArea();
        JavaLanguageSupport groovyLanguageSupport;
        groovyLanguageSupport = (JavaLanguageSupport) LanguageSupportFactory.get()
                .getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
        groovyLanguageSupport.setJarManager langSupport.addFileSourceToRsta.jarManager
        textArea.groovyLanguageSupport = groovyLanguageSupport
        groovyLanguageSupport.install(textArea);

//        JrrClassUtils.setFieldValue(langSupport.groovyLanguageSupport, "jarManager",
//                langSupport.addFileSourceToRsta.jarManager);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        textArea.addSupport();
//        textArea.groovyLanguageSupport = langSupport.groovyLanguageSupport;
        if(langSupport.osInegrationClient!=null) {
            textArea.addExternalMemberClickedListener(
                    new RstaOpenMember(langSupport.osInegrationClient));
        }
    }

    public Component getScriptSource() {
        return scrollPane;
    }

    public void prepareAndRun() {
        textArea.removeAllLineHighlights();
    }

    public void highLightLineAsError(int line) throws BadLocationException {
        textArea.addLineHighlight(line, Color.PINK);
    }

    private void highLightCurrentExecutingLine(int line) throws BadLocationException {
        textArea.removeAllLineHighlights();
        textArea.addLineHighlight(line, Color.cyan);
    }

    public volatile Thread codeThread;

    public void codeStarted() {
        codeThread = Thread.currentThread();
        synchronized (codeExecutionHilighter) {
            codeExecutionHilighter.notify();
        }
    }

    public void additionalHilighter() {

    }

    boolean isStopCodeExecutionHilighterThread = false

    private Thread codeExecutionHilighter = new Thread("CodeExecutionHilighter") {
        @Override
        public void run() {
            while (!isStopCodeExecutionHilighterThread) {
                try {
                    codeExecutionHilighterM()
                } catch (Exception e) {
                    log.log(Level.SEVERE, "", e);
                }
            }
        }
    };

    void codeExecutionHilighterM(){
        Thread codeThread2 = codeThread;
        if (codeThread2 == null) {
            log.info("thread value is null");
        } else {
            StackTraceElement[] stackTraces = codeThread2.getStackTrace();
            final int lineNumber2 = getLineNumberFromStackTrace(stackTraces);
            if (lineNumber2 >= 0) {
                SwingUtilities.invokeLater {
                    try {
                        if (codeThread != null) {
                            highLightCurrentExecutingLine(lineNumber2);
                        }
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "", e);
                    }
                }

            }
            additionalHilighter();
        }
        synchronized (codeExecutionHilighter) {
            if (codeThread2 == null) {
                codeExecutionHilighter.wait();
            } else {
                codeExecutionHilighter.wait(1000);
            }
        }
    }

    void stopCodeExecutionHilighterThread(){
        isStopCodeExecutionHilighterThread = true;
        synchronized (codeExecutionHilighter){
            codeExecutionHilighter.notifyAll()
        }
    }

    public int getLineNumberFromStackTrace(StackTraceElement[] stackTraces) {
        StackTraceElement stackTraceElementF = null;
        for (StackTraceElement stackTraceElement : stackTraces) {
            if (stackTraceElement.getClassName().startsWith(generatedClassName)) {
                stackTraceElementF = stackTraceElement;
                break;
            }
        }
        if (stackTraceElementF != null) {
            int lineNumber = stackTraceElementF.getLineNumber();
            if (lineNumber > 0) {
                final int lineNumber2 = lineNumber - 1;
                // log.info(lineNumber2 + "");
                return lineNumber2;
            } else {
                log.warning("wrong line number : " + stackTraceElementF);
            }
        }
        return -1;
    }

    public void codeStopped() {
        codeThread = null;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.removeAllLineHighlights();
            }
        });
    }

    public int getSelectionStart() {
        return textArea.getSelectionStart();
    }

    public void runAfterInSwingThread() {

    }

    public void close() {
    }

    public boolean isEditable() {
        return textArea.isEditable();
    }

    public void setEditable(final boolean editable) {
        textArea.setEditable(editable);
    }

    public void setColumns(final int i) {
        textArea.setColumns(i);

    }

    public RTextScrollPane getScrollPane() {
        return scrollPane;
    }

    public Component getComponent() {
        return scrollPane;
    }

    public void replaceRange(String value, int start, int end) {
        textArea.replaceRange(value, start, end);

    }

    public Caret getCaret() {
        return textArea.getCaret();
    }

    public void setCurstorToStart() {
        textArea.select(0, 0);
    }

    public void setSyntaxEditingStyle(String newStyle) {
        textArea.setSyntaxEditingStyle(newStyle);

    }

    public void setLineNumbersEnabled(boolean selected) {
        scrollPane.setLineNumbersEnabled(selected);

    }

    public void addMenuItem(JMenuItem menuItem) {
        menuItems.add(menuItem);
        textArea.addMenuItem(menuItem);
    }

}
