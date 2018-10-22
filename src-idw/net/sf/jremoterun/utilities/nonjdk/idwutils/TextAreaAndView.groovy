package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JPanelBorderLayout
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.JPanel
import java.awt.BorderLayout
import java.util.logging.Logger

@CompileStatic
class TextAreaAndView {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    RSyntaxTextArea textArea = new RSyntaxTextArea();

    public JPanel panel = new JPanelBorderLayout()

    RTextScrollPane scrollPane = new RTextScrollPane(textArea, false);

    View view;

    TextAreaAndView(String title) {
        panel.add(scrollPane, BorderLayout.CENTER)
        view = new View(title, null, panel);
    }
}
