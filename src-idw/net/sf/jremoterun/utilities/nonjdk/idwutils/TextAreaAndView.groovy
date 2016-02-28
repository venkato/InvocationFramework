package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane

import java.util.logging.Logger

@CompileStatic
class TextAreaAndView {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    RSyntaxTextArea textArea = new RSyntaxTextArea()

    RTextScrollPane scrollPane = new RTextScrollPane(textArea, false)

    View view

    TextAreaAndView(String title) {
        view = new View(title, null, scrollPane);
    }
}
