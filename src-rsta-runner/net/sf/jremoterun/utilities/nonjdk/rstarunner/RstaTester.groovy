package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import junit.framework.TestCase
import net.infonode.docking.View
import net.infonode.util.Direction
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtilsStarter
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwWindowFinder
import net.sf.jremoterun.utilities.nonjdk.swing.SimpleFrameCreator
import net.sf.jremoterun.utilities.nonjdk.tcpmon.Tcpmon

import javax.swing.*
import java.util.logging.Logger

@CompileStatic
class RstaTester extends TestCase {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void test1() {
        JdkLogFormatter.setLogFormatter()
        JFrame frame = SimpleFrameCreator.createAppFrame("RstaTester")
        File code = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/rstatester.groovy");
        RstaRunner rstaRunner = new RstaRunner(code)
        frame.getContentPane().add(rstaRunner.getMainPanel())
        frame.setVisible(true)
        Thread.sleep(Long.MAX_VALUE)
    }

    void test3() {
        JFrame frame = SimpleFrameCreator.createAppFrame("RstaTester")
        File code = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/rstatester.groovy");
        RstaRunnerWithStackTrace rstaRunner = new RstaRunnerWithStackTrace(code)
        frame.getContentPane().add(rstaRunner.getMainPanel())
        frame.setVisible(true)
        Thread.sleep(Long.MAX_VALUE)
    }

    void test2() {
        JdkLogFormatter.setLogFormatter();
        log.info "${3}"
        File f = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/Sam.groovy");
        Tcpmon tcpmon = new Tcpmon(f);
        log.info "${1}"
        JFrame frame = SimpleFrameCreator.createAppFrameIdw("RstaTester", tcpmon.notebook)
        log.info "${2}"
        tcpmon.notebook.addTab(new View("SimpleLabel",null,new JLabel("simple label")))
        IdwWindowFinder finder = new IdwWindowFinder()
        tcpmon.notebook.addTab(finder.view)
//        finder.view.undock(new Point(100,100))
        frame.setVisible(true)
        Thread.sleep(Long.MAX_VALUE)
    }


    void testIdwWindowsList() {
        JdkLogFormatter.setLogFormatter();
//		InitializerHomePcAll.init()
        log.info "${3}"
        File f = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/Sam.groovy");
        Tcpmon tcpmon = new Tcpmon(f);
        log.info "${1}"
        JFrame frame = SimpleFrameCreator.createAppFrameIdw("RstaTester", tcpmon.notebook)
        log.info "${2}"
        frame.setVisible(true)
        IdwUtilsStarter.rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);

        IdwWindowFinder finder = new IdwWindowFinder()
//		tcpmon.notebook.addTab()
        IdwUtilsStarter.rootWindow.getWindowBar(Direction.LEFT).addTab(finder.view);
//        finder.view.undock(new Point(100,100))
//        net.sf.jremoterun.utilities.nonjdk.idwutils.SwitchWindows.register()
        Thread.sleep(Long.MAX_VALUE)
    }

    void testRstaTesterWithStacks() {
        JdkLogFormatter.setLogFormatter();
//		InitializerHomePcAll.init()
        net.sf.jremoterun.utilities.nonjdk.InitGeneral.init1()
//        Log4j2Utils.setLog4jAppender()
        log.info "${3}"
        File f = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/Sam.groovy");
        Tcpmon tcpmon = new Tcpmon(f);
        log.info "${1}"
        JFrame frame = SimpleFrameCreator.createAppFrameIdw("RstaTester", tcpmon.notebook)
        log.info "${2}"
        frame.setVisible(true)
        IdwUtilsStarter.rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);

        IdwWindowFinder finder = new IdwWindowFinder()
//		tcpmon.notebook.addTab()
        IdwUtilsStarter.rootWindow.getWindowBar(Direction.LEFT).addTab(finder.view);
        File code = JrrClassUtils.getResourceFromCurrentClassLoader("tcpmon/rstatester.groovy");
        RstaRunnerWithStackTrace2 rstaRunner = new RstaRunnerWithStackTrace2(code)
        tcpmon.notebook.addTab(rstaRunner.mainPanel3)
//        finder.view.undock(new Point(100,100))
//        net.sf.jremoterun.utilities.nonjdk.idwutils.SwitchWindows.register()
        Thread.sleep(Long.MAX_VALUE)
    }



}
