package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.junit.Test

import javax.swing.JFrame
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class HeadLessModeSetter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void setHeadlessMode(boolean value){
        JrrClassUtils.setFieldValue(GraphicsEnvironment,'headless',value)
        assert GraphicsEnvironment.isHeadless() == value
    }

}
