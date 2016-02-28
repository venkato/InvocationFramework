package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.RootWindow
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.*
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdwStateReaderWriter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static void saveViewState(RootWindow rootWindow, File saveViewFIle, JFrame frame) throws IOException {
        FileRotate.rotateFile(saveViewFIle,100)
        ObjectOutputStream ooo = saveViewFIle.newObjectOutputStream()

        try {
            rootWindow.write(ooo, false);
            FrameLocationInfo frameLOcationInfo = new FrameLocationInfo();
            frameLOcationInfo.location = frame.getLocation();
            frameLOcationInfo.dimension = frame.getSize();
            ooo.writeObject(frameLOcationInfo);
            // i++;
            // File file = new File(saveViewFIle.getValue()
            // .getParentFile(), i + ".ser");
            // saveViewFIle.setValue(file);
            ooo.flush();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            log.log(Level.WARNING,"", e1);
        } finally {
            try {
                ooo.close()
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                log.warn("", e1);
            }
        }
    }



    public static void readViewState(File file, JFrame frame,RootWindow rootWindow) throws Exception {
        ObjectInputStream ooo = file.newObjectInputStream()
        try {
            readViewState(ooo,frame,rootWindow);
        }finally{
            ooo.close()
        }
    }

    public static void readViewState(ObjectInputStream ooo, JFrame frame,RootWindow rootWindow) throws Exception {
        rootWindow.read(ooo);
        FrameLocationInfo frameLOcationInfo = (FrameLocationInfo) ooo
                .readObject();
        frame.setLocation(frameLOcationInfo.location);
        frame.setSize(frameLOcationInfo.dimension);
    }
}
