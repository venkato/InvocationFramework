package net.sf.jremoterun.utilities.nonjdk.swing


import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities

import javax.swing.*
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class QrCodeCreator extends JPanel {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public BufferedImage image;
//    public int multFactor = 4
//    public ViewAndPanel viewAndPanel;
    private QrCodeCreator qrCodeCreator = this;

    QrCodeCreator() {
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        if (image != null) {
            grphcs.drawImage(image, 0, 0, null);
        }
    }

    protected void generateQrCodeImpl(String messsage) {
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int min1 = getSizeImage()
        BitMatrix byteMatrix = qrCodeWriter.encode(messsage, BarcodeFormat.QR_CODE, min1, min1, hintMap);
        int crunchifyWidth = byteMatrix.getWidth();
        image = new BufferedImage(crunchifyWidth, crunchifyWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, crunchifyWidth, crunchifyWidth);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < crunchifyWidth; i++) {
            for (int j = 0; j < crunchifyWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
    }

    void generateQrCode(String messsage) {
        if (messsage == null || messsage.isEmpty()) {
            image = null;
            return;
        }
        try {
            generateQrCodeImpl(messsage)
            qrCodeCreator.repaint();
        } catch (WriterException ex) {
            log.log(Level.SEVERE, 'failed write', ex);
            JrrUtilities.showException('failed write', ex)
        }
    }

    int getSizeImage(){
        int width3 = getWidth();
        int height3 = getHeight()
        int size12=Math.min(width3,height3)
        return size12
    }

    int getWidth1() {
        return getWidth()
    }


    int getHeight1() {
        return getHeight()
    }


}
