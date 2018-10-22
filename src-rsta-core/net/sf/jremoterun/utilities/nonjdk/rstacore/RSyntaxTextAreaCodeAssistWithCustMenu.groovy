package net.sf.jremoterun.utilities.nonjdk.rstacore;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.rsta.ac.java.custom.RSyntaxTextAreaCodeAssist

import javax.swing.JPopupMenu;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class RSyntaxTextAreaCodeAssistWithCustMenu extends RSyntaxTextAreaCodeAssist{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    protected void appendFoldingMenu(JPopupMenu popupMenu) {
        super.appendFoldingMenu(popupMenu);
        appendFoldingMenu2(popupMenu);
    };

    abstract void appendFoldingMenu2(JPopupMenu popupMenu);

}
