package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import net.sf.jremoterun.utilities.nonjdk.log.threadfilter.Log4j2ThreadAppender
import org.apache.logging.log4j.core.LogEvent

import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.Component
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class RstaJavaEditor extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    View view

    RstaJavaEditor(File file) {
        super(file)
        view= new View(file.name,null,panel)
    }

    RstaJavaEditor(String name,String text2) {
        super(text2)
        view= new View(name,null,panel)
    }
}