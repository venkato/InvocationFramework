package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.FloatingWindow
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.tabbedpanel.TabbedPanel
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JComponent
import javax.swing.JOptionPane
import java.awt.Component
import java.util.logging.Logger

@CompileStatic
class IdwActions {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    private static void regiteterKeyStokes2(JComponent component, IdwShortcuts ks, Closure<Boolean> impl) {
        component.registerKeyboardAction({
            try {
                impl.call()
            } catch (Exception e2) {
                log.info("${component}", e2);
            }
        }, ks.keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    static void regiteterKeyStokes(JComponent component) {
        regiteterKeyStokes2(component, IdwShortcuts.nextUpperTab, {
            switchToNext(component, true)
        });

        regiteterKeyStokes2(component, IdwShortcuts.nextTab, {
            switchToNext(component, false)
        });

        regiteterKeyStokes2(component, IdwShortcuts.prevoiusTab, {
            switchToPrevoius(component, false)
        });

        regiteterKeyStokes2(component, IdwShortcuts.prevoiusUpperTab, {
            switchToPrevoius(component, true)
        });

        regiteterKeyStokes2(component, IdwShortcuts.focusOppositeTab, {
            splitSwitch(component)
        });

        regiteterKeyStokes2(component, IdwShortcuts.switchSplitLayout, {
            switchSpliptWondowLayout(component)
        });

        regiteterKeyStokes2(component, IdwShortcuts.swapTabs, {
            swapTab(IdwUtils.getDockerWindowWithPopmenu(component))
        });

        regiteterKeyStokes2(component, IdwShortcuts.moveOppositeTab, {
            log.info("cp1")
            moveTab(IdwUtils.getDockerWindowWithPopmenu(component))
        });
        regiteterKeyStokes2(component, IdwShortcuts.rename, {
            log.info("cp1")
            DockingWindow dw = IdwUtils.getDockerWindowWithPopmenu(component);
            IdwPopupMenuFactory.renameWindow(dw)
            return true
        });
        regiteterKeyStokes2(component, IdwShortcuts.moveToDialog, {
            log.info("cp1")
            DockingWindow dw = IdwUtils.getDockerWindowWithPopmenu(component)
            IdwMoveToNewTab.showMoveToMenu2(dw)
            return true
        });
        regiteterKeyStokes2(component, IdwShortcuts.maximaze, {
            log.info("cp3")
            return maximazeIdw(component)
        });
    }

    static boolean maximazeIdw(Component component) {
        DockingWindow dw = IdwUtils.getDockerWindowWithPopmenu(component)
        if (dw.windowParent instanceof TabWindow) {
            dw = dw.windowParent
        }

        FloatingWindow fw;
        if (fw == null && dw instanceof FloatingWindow) {
            fw = dw as FloatingWindow
        }
        if (fw == null && dw.windowParent instanceof FloatingWindow) {
            fw = dw.windowParent as FloatingWindow
        }
        //log.info "${dw.windowParent.class.name}"
//        log.info "${dw.windowParent.windowParent.class.name}"
        if (fw == null) {
            log.info "not fw"
            if (dw.isMaximizable()) {
                log.info "isMaximized = ${dw.isMaximized()}"
                if (dw.maximized) {
                    dw.restore()
                } else {
                    dw.maximize()
                }
                return true
            } else {
                log.info "not maximazable"
                return false
            }
        } else {
            log.info "maximaze fw in FloatingWindow"
            return IdwUtils2.maximazeFw(fw);
        }
    }

    static boolean splitSwitch(Component component) {
        SplitWindow splitWindow = IdwUtils.getParentIdwWindowSpecial3(component, SplitWindow);
        if (splitWindow == null) {
            log.info("can't find parent split ${component}");
            JOptionPane.showMessageDialog(component, "can't find parent split ${component}");
            return false;
        }
        DockingWindow child1 = IdwUtils.getOppositeSplit(splitWindow);
        IdwUtils.selectLastFocusedChild(child1);
        return true;
    }

    static boolean swapTab(DockingWindow dw1) {
        TabWindow tab1 = IdwUtils.getParentIdwWindowSpecial(dw1, TabWindow);
        if (tab1 == null) {
            log.info("can't find parent tab ${dw1}");
            JOptionPane.showMessageDialog(dw1, "can't find parent tab ${dw1}");
            return false
        }
        SplitWindow splitWindow = IdwUtils.getParentIdwWindowSpecial3(tab1, SplitWindow);
        if (splitWindow == null) {
            log.info("can't find parent split ${tab1}");
            JOptionPane.showMessageDialog(dw1, "can't find parent split ${tab1}")
            return false;
        }
        DockingWindow child1 = IdwUtils.getOppositeSplit(splitWindow);
        if (!(child1 instanceof TabWindow)) {
            log.info("can't find parent opposite tab window ${splitWindow}");
            JOptionPane.showMessageDialog(dw1, "can't find parent opposite tab window ${splitWindow}")
            return false;
        }
        TabWindow tab2 = (TabWindow) child1;
        if (tab1.getChildWindowCount() <= 1) {
            log.info "tab1 need more then 1 tab : ${tab1}"
            JOptionPane.showMessageDialog(dw1, "tab2 need more then 1 tab : ${tab2}")
            return false
        }
        if (tab2.getChildWindowCount() <= 1) {
            log.info "tab2 need more then 1 tab : ${tab2}"
            JOptionPane.showMessageDialog(dw1, "tab2 need more then 1 tab : ${tab2}")
            return false
        }
        DockingWindow dw2 = tab2.getSelectedWindow();
        log.info("doing swap ${dw1} ${dw2}")
        tab2.addTab(dw1)
        tab1.addTab(dw2)
        return true;
    }

    static boolean moveTab(DockingWindow dw1) {
        TabWindow tab1 = IdwUtils.getParentIdwWindowSpecial(dw1, TabWindow);
        if (tab1 == null) {
            log.info("can't find parent tab ${dw1}");
            JOptionPane.showMessageDialog(dw1, "can't find parent tab ${dw1}");
            return false
        }
        SplitWindow splitWindow = IdwUtils.getParentIdwWindowSpecial(tab1, SplitWindow); ;
        if (splitWindow == null) {
            log.info("can't find parent split ${tab1}");
            JOptionPane.showMessageDialog(dw1, "can't find parent split ${tab1}");
            return false;
        }
        DockingWindow child1 = IdwUtils.getOppositeSplit(splitWindow);
        if (!(child1 instanceof TabWindow)) {
            log.info("can't find parent opposite tab window ${splitWindow}");
            JOptionPane.showMessageDialog(dw1, "can't find parent opposite tab window ${splitWindow}");
            return false;
        }
        TabWindow tab2 = (TabWindow) child1;
        tab2.addTab(dw1)
        return true;
    }

    static boolean switchSpliptWondowLayout(Component component) {
        SplitWindow splitWindow = IdwUtils.getParentIdwWindowSpecial3(component, SplitWindow);
        if (splitWindow == null) {
            log.info "failed find parent split window ${component}"
            return false
        }
        splitWindow.setHorizontal(!splitWindow.isHorizontal());
        log.info "layout switched"
        return true
    }

    static boolean switchToNext(Component component, boolean upper) {
        TabWindow parentTabWindow = IdwUtils.getParentIdwWindowSpecial3(component, TabWindow);
        if (parentTabWindow == null) {
            log.info("can't find parent tab ${component}");
            return false;
        }
        if (parentTabWindow.getChildWindowCount() == 1) {
            return switchToNext(parentTabWindow, upper);
        }
        if (upper) {
            return switchToNext(parentTabWindow, false);
        }

        TabbedPanel tabbedPanel = IdwUtils.getTabbedPanel(parentTabWindow);
        int tabIndex = tabbedPanel.getTabIndex(tabbedPanel.getSelectedTab());
//        log.info("tab index :" + tabIndex);
        if (tabIndex >= tabbedPanel.getTabCount() - 1) {
            log.info("up conner");
        } else {
            int newSelect = tabIndex + 1;
            parentTabWindow.setSelectedTab(newSelect);
            IdwUtils.selectLastFocusedChild(parentTabWindow.getChildWindow(newSelect));
            return true;
        }
        return false;
    }

    static boolean switchToPrevoius(Component component, boolean upper) {
        TabWindow parentTabWindow = IdwUtils.getParentIdwWindowSpecial3(component, TabWindow);
        if (parentTabWindow == null) {
            log.info("can't find parent tab ${component}");
            return false;
        }
        if (parentTabWindow.getChildWindowCount() == 1) {
            return switchToPrevoius(parentTabWindow, upper);
        }
        if (upper) {
            return switchToPrevoius(parentTabWindow, false);
        }
        TabbedPanel tabbedPanel = IdwUtils.getTabbedPanel(parentTabWindow);
        int tabIndex = tabbedPanel.getTabIndex(tabbedPanel.getSelectedTab());
//        log.info("tab index :" + tabIndex);
        if (tabIndex < 1) {
            log.info("left conner");
        } else {
            int newSelect = tabIndex - 1;
            parentTabWindow.setSelectedTab(newSelect);
            IdwUtils.selectLastFocusedChild(parentTabWindow.getChildWindow(newSelect));
            return true;
        }
        return false;
    }
}
