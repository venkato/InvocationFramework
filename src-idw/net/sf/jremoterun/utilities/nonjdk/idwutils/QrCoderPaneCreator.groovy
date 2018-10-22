package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.QrCodeCreator

import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener;
import java.util.logging.Logger;

@CompileStatic
class QrCoderPaneCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public TextAreaAndView areaAndView = new TextAreaAndView('Text')
    public QrCodeCreator qrCodeCreator = new QrCodeCreator()
    public ViewAndPanel imageView = new ViewAndPanel('Code', qrCodeCreator);

    public SplitWindow splitWindow = new SplitWindow(true, areaAndView.view, imageView.view)

    QrCoderPaneCreator() {
        addListener1()
        IdwUtils.setTitle(splitWindow,'QR code')
    }

    void addListener1() {
        areaAndView.textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            void insertUpdate(DocumentEvent e) {
                updateText()
            }

            @Override
            void removeUpdate(DocumentEvent e) {
                updateText()
            }

            @Override
            void changedUpdate(DocumentEvent e) {
                updateText()
            }

        })
    }


    void updateText() {
        SwingUtilities.invokeLater {
            qrCodeCreator.generateQrCode(areaAndView.textArea.getText())
        }
    }

}
