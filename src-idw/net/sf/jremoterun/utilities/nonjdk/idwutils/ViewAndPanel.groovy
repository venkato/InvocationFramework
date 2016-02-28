package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JPanelBorderLayout

import javax.swing.*
import java.awt.BorderLayout
import java.util.logging.Logger

@CompileStatic
class ViewAndPanel {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final JPanel panel

    public final View view;

    ViewAndPanel(String name,JPanel panel) {
        this.panel = panel;
        view = new View(name, null, panel)
    }

    ViewAndPanel(String name) {
        this(name, new JPanelBorderLayout())
    }
}
