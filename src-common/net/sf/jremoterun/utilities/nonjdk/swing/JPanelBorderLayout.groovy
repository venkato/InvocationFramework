package net.sf.jremoterun.utilities.nonjdk.swing;

import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JPanel
import java.awt.BorderLayout;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JPanelBorderLayout extends JPanel{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JPanelBorderLayout() {
        super(new BorderLayout())
    }
}
