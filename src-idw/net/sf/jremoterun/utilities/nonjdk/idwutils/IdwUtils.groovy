package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.*
import net.infonode.tabbedpanel.TabbedPanel
import net.infonode.util.Direction
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.CreationInfo
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtilsParent

import javax.swing.*
import java.awt.*
import java.util.logging.Logger

@CompileStatic
class IdwUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static CreationInfo addMaxCalled

    static TabbedPanel getTabbedPanel(TabWindow parentTabWindow)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        return (TabbedPanel) JrrClassUtils.getFieldValue(parentTabWindow, "tabbedPanel");
    }


    static void setTitle(DockingWindow dockingWindow, String title) {
        JrrSwingUtils.invokeNowOrLaterInSwingThread {
            dockingWindow.getWindowProperties().setTitleProvider(new MyDockingWindowTitleProvider(title))
        }
    }

    static void setClosable(DockingWindow view, boolean enabled) {
        JrrSwingUtils.invokeNowOrLaterInSwingThread {
            view.getWindowProperties().closeEnabled = enabled
        }
    }

    static void setVisible(DockingWindow dockingWindow) {
        JrrSwingUtils.invokeNowOrLaterInSwingThread {
            dockingWindow.requestFocus()
            dockingWindow.requestFocusInWindow()
            dockingWindow.makeVisible()
            Window window = JrrSwingUtilsParent.findParentWindow(dockingWindow, Window)
            if (window == null) {
                log.info "failed find window for ${dockingWindow}"
            } else {
                window.toFront();
            }
            log.info "window bring to front : ${dockingWindow}"
        }
    }


    public static void addMaxButton(RootWindow rootWindow) {
        if (addMaxCalled != null) {
            throw addMaxCalled.createExc()
        }
        addMaxCalled = new CreationInfo()
        JrrSwingUtils.invokeNowOrLaterInSwingThread {
            rootWindow.addListener(new DockingWindowAdapter() {

                @Override
                public void windowAdded(final DockingWindow addedToWindow, final DockingWindow addedWindow) {

                    // If the added window is a floating window, then update it
                    if (addedWindow instanceof FloatingWindow)
                        IdwUtils2.updateFloatingWindow((FloatingWindow) addedWindow);
                }
            });
        }
    }


    public static JPopupMenu getPopupMenuForMenuItem1(final Container component) {
        if (component instanceof JPopupMenu) {
            return (JPopupMenu) component;
        }
        return getPopupMenuForMenuItem1(component.getParent());
    }

    public static DockingWindow getDockerWindow(Component component) {
        return JrrSwingUtilsParent.findParentWindow(component, DockingWindow)
    }

    static DockingWindow getDockerWindowWithPopmenu(Component component) {
        if (component instanceof DockingWindow) {
            DockingWindow dockingWindow1 = (DockingWindow) component;
            return dockingWindow1
        }
        if (component instanceof JPopupMenu) {
            JPopupMenu popupMenu = component as JPopupMenu
            Component invoker = popupMenu.invoker
            log.info "invoker = ${invoker}"
            return getDockerWindowWithPopmenu(invoker);
        }
        if (component instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem) component;
            JPopupMenu popupMenu = getPopupMenuForMenuItem1(mi)
            return getDockerWindowWithPopmenu(popupMenu);
        }
        Container parent = component.getParent();
        if (parent instanceof DockingWindow) {
            DockingWindow new_name = (DockingWindow) parent;
            return new_name;
        }
        return getDockerWindowWithPopmenu(parent);
    }

    static <T> T getParentIdwWindowSpecial3(Component component, Class<T> clazz) {
        Container parent = component.getParent()
        if (clazz.isInstance(parent)) {
            return parent as T;
        }
        if (DockingWindow.isAssignableFrom(clazz)) {
            DockingWindow pare3 = getDockerWindowWithPopmenu(parent);
            if (clazz.isInstance(pare3)) {
                return pare3 as T;
            }
            return getParentIdwWindowSpecial(pare3, clazz)
        }
        return getParentIdwWindowSpecial3(parent, clazz);
    }

    static <T> T getParentIdwWindowSpecial(DockingWindow dockingWindow, Class<T> clazz) {

        DockingWindow windowParent = dockingWindow.getWindowParent();
        if (windowParent == null) {
            return null;
        }
        if (clazz.isInstance(windowParent)) {
            return windowParent as T;
        }

        return getParentIdwWindowSpecial(windowParent, clazz);
    }


    public static void selectLastFocusedChild(DockingWindow dockingWindow1) {
        JrrSwingUtils.invokeNowOrLaterInSwingThread {
            DockingWindow dockingWindow = findLastFocusedChildDeep(dockingWindow1);
            if (dockingWindow == null) {

            } else {
                if (dockingWindow instanceof View) {
                    View new_name = (View) dockingWindow;
                    Component component = new_name.getComponent();
                    component.requestFocus();
                    component.requestFocusInWindow();

                } else {
                    dockingWindow.requestFocus();
                    dockingWindow.requestFocusInWindow();
                }
                setVisible(dockingWindow)
            }
        }
    }


    static TabWindow getOppositeSplitTab(DockingWindow dockingWindow) {
        TabWindow parentTabWindow = getParentIdwWindowSpecial(dockingWindow, TabWindow);
        if(parentTabWindow==null){
            throw new Exception("failed find parent tab for ${dockingWindow}")
        }

        SplitWindow parentSplitWindow = getParentIdwWindowSpecial(parentTabWindow, SplitWindow); ;
        if (parentSplitWindow != null) {
            DockingWindow oppositeSplit = getOppositeSplit(parentSplitWindow);
            if (oppositeSplit instanceof TabWindow) {
                parentTabWindow = (TabWindow) oppositeSplit;
            }
        }
        return parentTabWindow;
    }

    static DockingWindow getOppositeSplit(SplitWindow splitWindow) {
        DockingWindow lastFocusedChildWindow = splitWindow.getLastFocusedChildWindow();
        DockingWindow child1 = splitWindow.getChildWindow(0);
        if (child1 != lastFocusedChildWindow) {
//            log.info("select 1");
            return child1;
        }
//        log.info("select 2");
        DockingWindow child2 = splitWindow.getChildWindow(1);
        return child2;
    }

    // stagnge methods
    public static TabWindow findVisibleTabWindow(DockingWindow tabWindow, TabWindow defaultTabWindow) {
        TabWindow foundTabWindow2 = getParentIdwWindowSpecial(tabWindow, TabWindow);
        if (foundTabWindow2 == null) {
            foundTabWindow2 = new TabWindow();
            defaultTabWindow.addTab(foundTabWindow2);
        }
        return foundTabWindow2;
    }

    // stagnge methods
    public static void addTab(TabWindow tabWindow, DockingWindow view, TabWindow defaultWindow) {
        if (true) {
            TabWindow tabWindow2 = mapInvisibaleViewdows.get(tabWindow);
            if (tabWindow2 != null) {
                log.info("replacin inv " + tabWindow.getTitle());
                tabWindow = tabWindow2;
            }
        }
        Window window = JrrSwingUtilsParent.findParentWindow(tabWindow, Window);
        if (window == null) {
            log.info("window not visible " + tabWindow.getTitle());
            tabWindow = createNewWindow(tabWindow);
            defaultWindow.addTab(tabWindow)
        }
        tabWindow.addTab(view);
    }


    public static IdentityHashMap<TabWindow, TabWindow> mapInvisibaleViewdows = new IdentityHashMap();

    // stagnge methods
    public static TabWindow createNewWindow(TabWindow tabWindow) {
        TabWindow tabWindow22 = new TabWindow();
        tabWindow22.getWindowProperties().setTitleProvider(
                new MyDockingWindowTitleProvider(tabWindow.getTitle()));
        mapInvisibaleViewdows.put(tabWindow, tabWindow22);
        return tabWindow22;
    }

    // Used in tcpmon
    public static void setLeftBar(final TabWindow tabWindow) {
        tabWindow.getTabWindowProperties().getTabbedPanelProperties()
                .setTabAreaOrientation(Direction.RIGHT);
        tabWindow.getTabWindowProperties().getTabProperties()
                .getTitledTabProperties().getNormalProperties()
                .setDirection(Direction.DOWN);
    }


    static java.util.List<DockingWindow> getChildrenDeep(DockingWindow rw) {
        if (rw == null) {
            throw new NullPointerException("argument is null")
        }
        Collection<DockingWindow> res = getChildrenDirect(rw)
        java.util.List<DockingWindow> res2 = new ArrayList<>(res)
        res2.each {
            res.addAll getChildrenDeep(it)
        }
        return res
    }

    static java.util.List<DockingWindow> getChildrenDirect(DockingWindow rw) {
        if (rw == null) {
            throw new NullPointerException("argument is null")
        }
        java.util.List<DockingWindow> res = []
        int c = rw.getChildWindowCount();
        for (int i = 0; i < c; i++) {
            DockingWindow window = rw.getChildWindow(i);
            res.add window
        }
        return res
    }


    public static DockingWindow findLastFocusedChildDeep(DockingWindow childWindow) {
        if (childWindow instanceof View) {
            View new_name = (View) childWindow;
            return new_name;
        } else if (childWindow instanceof TabWindow) {
            TabWindow new_name = (TabWindow) childWindow;
            DockingWindow selectedWindow = new_name.getSelectedWindow();
            return findLastFocusedChildDeep(selectedWindow);
        } else {
            DockingWindow lastFocusedChildWindow = childWindow.getLastFocusedChildWindow();
            if (lastFocusedChildWindow == null) {
                return null;
            }
            return findLastFocusedChildDeep(lastFocusedChildWindow);
        }
    }


    static boolean isTopLevelWindow(DockingWindow dockingWindow) {
        DockingWindow parent = dockingWindow.getWindowParent();
        return parent == null || dockingWindow instanceof FloatingWindow || parent instanceof RootWindow
    }

}
