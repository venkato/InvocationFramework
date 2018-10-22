package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.RootWindow
import net.infonode.properties.propertymap.JrrIdwPropertyMapManager;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtilsStarter
import net.sf.jremoterun.utilities.nonjdk.swing.JPanelBorderLayout
import net.sf.jremoterun.utilities.nonjdk.swing.SimpleFrameCreator

import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Container
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

@CompileStatic
class IdwFrameCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JFrame frame;
    JPanel rootPanel = new JPanelBorderLayout()

    IdwFrameCreator(String frameName,DockingWindow dockingWindow){
        JrrIdwPropertyMapManager.setManager()
        RootWindow window = IdwUtilsStarter.createRootWindow()
        window.setWindow(dockingWindow)
        frame = new JFrame(frameName);
        Container contentPane = frame.getContentPane()
        contentPane.add(rootPanel, BorderLayout.CENTER);
        rootPanel.add(window,BorderLayout.CENTER);
    }


    void setIcon(File  f){
        SimpleFrameCreator.setIcon(frame,f);

    }

    void setIcon(InputStream iconRes){
        SimpleFrameCreator.setIcon(frame,iconRes);

    }


    void setAlwaysOnTopFalse(){
        frame.setAlwaysOnTop(false);
    }

    void setExitOnClose(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void setMaximazedState(){
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);    }

    void setDefaultLocationAndSize(){
        frame.setSize(1201, 284);
        frame.setLocation(706, -8);
    }


}
