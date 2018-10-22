package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.RootWindow
import net.infonode.docking.properties.RootWindowProperties
import net.infonode.docking.util.DockingUtil
import net.infonode.docking.util.ViewMap
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.CreationInfo

import java.util.logging.Logger

@CompileStatic
class IdwUtilsStarter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ViewMap viewMap = new ViewMapListener()
    public static RootWindow rootWindow
    public static IdwPopupMenuFactory idwPopupMenuFactory
    private static CreationInfo creationStack
    public static RootWindowProperties idwProperties = new RootWindowProperties();

    public static RootWindow createRootWindow() {
        if (creationStack != null) {
            log.info("created before", creationStack.callStack)
            log.info("created before", new Exception())
            throw creationStack.createExc();
        }
        creationStack = new CreationInfo()
        rootWindow = DockingUtil.createRootWindow(viewMap, true)
        idwPopupMenuFactory = new IdwPopupMenuFactory(viewMap)
        rootWindow.setPopupMenuFactory(idwPopupMenuFactory)
        IdwUtils.addMaxButton(rootWindow)
//        SwitchWindows.register()
        rootWindow.getRootWindowProperties().addSuperObject(idwProperties)
        return rootWindow
    }

    static void useFrames(boolean useFrames){
        IdwUtilsStarter.idwProperties.getFloatingWindowProperties().setUseFrame(useFrames)
    }


}
