package net.sf.jremoterun.utilities.nonjdk.swing;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ClipboardUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void setClipboardContent(String content) {
        StringSelection selection = new StringSelection(content);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }


    static String getHtmlFromClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        Object data = t.getTransferData(DataFlavor.allHtmlFlavor);
        return data as String
    }

}
