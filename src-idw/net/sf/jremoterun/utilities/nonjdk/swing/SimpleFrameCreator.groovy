package net.sf.jremoterun.utilities.nonjdk.swing

import net.infonode.docking.DockingWindow
import net.infonode.docking.RootWindow;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtilsStarter

import javax.imageio.ImageIO
import javax.swing.JFrame
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class SimpleFrameCreator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void setIcon(JFrame frame, InputStream iconRes){
        assert iconRes!=null
        final BufferedImage image = ImageIO.read(iconRes);
        iconRes.close()
        assert image!=null
//		ImageIcon icon = new ImageIcon(image)
        frame.setIconImage(image);

    }

    static JFrame createAppFrameIdw(String windowName,DockingWindow dockingWindow){
        RootWindow window = IdwUtilsStarter.createRootWindow()
        window.setWindow(dockingWindow)
        JFrame frame = SimpleFrameCreator.createAppFrame(windowName)
        frame.getContentPane().add(window)
        return frame
    }


    static JFrame createAppFrame(String windowName){
        JFrame frame = new JFrame(windowName);
        frame.setSize(1201, 284);
        frame.setLocation(706, -8);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setAlwaysOnTop(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        return frame;

    }

}
