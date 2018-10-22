package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.FloatingWindow
import net.sf.jremoterun.utilities.JrrClassUtils


import javax.swing.JButton
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window
import java.util.logging.Logger

@CompileStatic
public class IdwUtils2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public static Window getRooWindow(Component component) {
        if (component instanceof Window) {
            Window new_name = (Window) component;
            return new_name;
        }
        return getRooWindow(component);
    }

    static boolean maximazeFw(FloatingWindow fw) {
        try {
            Window window = (Window) JrrClassUtils.getFieldValue(fw, "dialog");
            Rectangle screenWorkingArea = getScreenWorkingArea(window);
            log.info(''+screenWorkingArea);
            window.setLocation(screenWorkingArea.@x, screenWorkingArea.@y);
            window.setSize(screenWorkingArea.@width, screenWorkingArea.@height);
            return true;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            log.warn("", e1);
            return true;
        }
    }

    public static void updateFloatingWindow(final FloatingWindow fw) {
        JPanel panel = new JPanel(new FlowLayout());
        if (true) {
            JButton maximize = new JButton("Maximize");
            maximize.addActionListener {
                maximazeFw(fw)
            };
            panel.add(maximize);
        }
        if (true) {
            JButton maximize = new JButton("Restore location");
            maximize.addActionListener {

                try {
                    fw.dock()
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    log.warn("", e1);
                }
            };
            panel.add(maximize);
        }
        if (true) {
            JButton maximize = new JButton("Move to");
            maximize.addActionListener {

                try {
                    IdwMoveToNewTab.showMoveToMenu2(fw)
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    log.warn("", e1);
                }
            };
            panel.add(maximize);
        }
        fw.getRootPane().getContentPane().add(panel, BorderLayout.NORTH);
    }

    static public Rectangle getScreenWorkingArea(Window windowOrNull) {
        // From
        // http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-size-in-java
        Insets insets;
        Rectangle bounds;
        if (windowOrNull == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            insets = Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());
            bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        } else {
            GraphicsConfiguration gc = windowOrNull.getGraphicsConfiguration();
            insets = windowOrNull.getToolkit().getScreenInsets(gc);
            bounds = gc.getBounds();
        }
        bounds.@x += insets.left;
        bounds.@y += insets.top;
        bounds.@width -= (insets.left + insets.right);
        bounds.@height -= (insets.top + insets.bottom);
        return bounds;
    }


}
