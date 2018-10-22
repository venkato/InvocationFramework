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

    final RSyntaxTextAreaCodeAssistUndoFix  textArea;

    public GroovyShellGuiRSyntaxTextArea() {
        textArea = createTextArea();
        textArea.scrollPane = new RTextScrollPane(textArea, true);
        setEditable(true);
        textArea.setTabSize(2);
        textArea.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textArea.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.scrollPane.setIconRowHeaderEnabled(true);
        textArea.scrollPane.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {
                textArea.requestFocusInWindow();
            }
        });
        codeExecutionHilighter.start();
        textArea.setCodeFoldingEnabled(true);
    }

    protected RSyntaxTextAreaCodeAssistUndoFix createTextArea() {
        return new RSyntaxTextAreaCodeAssistUndoFix() {
            @Override
            void appendFoldingMenu2(JPopupMenu popupMenu) {
                appendFoldingMenu3(popupMenu);
            }
        };
    }

    protected void appendFoldingMenu3(JPopupMenu popupMenu){
        for (JMenuItem menuItem : menuItems) {
            popupMenu.add(menuItem);
        }

    }

    void addLangSupport(){
        textArea.addLangSupport()
    }

    @Deprecated
    public void setText(String text) {
        textArea.setText(text);
    }


//    abstract String getScriptText() throws IOException

     boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();

    }

    public void requestFocus() {
        textArea.requestFocus();
    }

    @Deprecated
    public String getText() {
        return textArea.getTextNormalized();
    }

    public String getSelectedText() {
        String selectedText = textArea.getSelectedText();
        if (selectedText == null) {
            return null;
        }
        selectedText = RSyntaxTextAreaCodeAssistUndoFix.nornalizeText(selectedText);
        return selectedText;
    }


    // used ?
    public Component getScriptSource() {
        return textArea.scrollPane;
    }

    // t:dual
    public void prepareAndRun() {
        textArea.removeAllLineHighlights();
    }

    // t:dual
    public void highLightLineAsError(int line) throws BadLocationException {
        textArea.addLineHighlight(line, Color.PINK);
    }

    // t:dual
    protected void highLightCurrentExecutingLine(int line) throws BadLocationException {
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

    protected void additionalHilighter() {

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

    void codeStopped() {
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

    @Deprecated
    public RTextScrollPane getScrollPane() {
        return textArea.scrollPane;
    }

    public Component getComponent() {
        return textArea.scrollPane;
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
        textArea.scrollPane.setLineNumbersEnabled(selected);

    }

    public void addMenuItem(JMenuItem menuItem) {
        menuItems.add(menuItem);
        textArea.addMenuItem(menuItem);
    }

}
