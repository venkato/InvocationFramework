package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.KeyStroke
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.util.logging.Logger

@CompileStatic
class SwitchWindows implements KeyEventDispatcher {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static Exception creationStack

    static void register() {
        if (creationStack != null) {
            log.info("creation first 1", creationStack)
            log.info("creation current", new Exception())
            throw new Exception("was created before")
        }
        creationStack = new Exception()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new SwitchWindows());
    }


    DockingWindow getLastFocus(DockingWindow dockingWindow) {
        DockingWindow childWindow = dockingWindow.getLastFocusedChildWindow()
        if (childWindow == null) {
            return dockingWindow
        }
        return getLastFocus(childWindow);
    }

    @Override
    boolean dispatchKeyEvent(KeyEvent e) {
        DockingWindow dw = getLastFocus(IdwUtilsStarter.rootWindow)
        if (dw == null) {
            log.info "no last focus window"
            return false
        }
//        log.info "${e}"
        try {
            switch (e) {
                case { isKeyMatcher(e, IdwShortcuts.moveOppositeTab) }:
                    IdwActions.moveTab(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.switchSplitLayout) }:
                    IdwActions.switchSpliptWondowLayout(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.focusOppositeTab) }:
                    IdwActions.splitSwitch(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.prevoiusTab) }:
                    IdwActions.switchToPrevoius(dw, false)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.nextTab) }:
                    IdwActions.switchToNext(dw, false)
                    break;

                case { isKeyMatcher(e, IdwShortcuts.prevoiusUpperTab) }:
                    IdwActions.switchToPrevoius(dw, true)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.nextUpperTab) }:
                    IdwActions.switchToNext(dw, true)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.swapTabs) }:
                    IdwActions.swapTab(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.rename) }:
                    IdwPopupMenuFactory.renameWindow(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.moveToDialog) }:
                    IdwMoveToNewTab.showMoveToMenu2(dw)
                    break;
                case { isKeyMatcher(e, IdwShortcuts.maximaze) }:
                    IdwActions.maximazeIdw(dw)
                    break;

            }
        } catch (Throwable e2) {
            log.info("${e}", e2);
            return false;
        }
        //IdwUtils.moveTab(IdwUtils.getDockerWindowWithPopmenu(input)
        return false
    }


    boolean isKeyMatcher(KeyEvent e, IdwShortcuts ks2) {
        KeyStroke ks = ks2.keyStroke
//        ks.get
        int gotModifiers = e.getModifiers();
        int neededModifiers = ks.getModifiers();
        if (e.getKeyCode() == ks.getKeyCode()) {

            boolean modfMatched = false;
            if (neededModifiers == 0) {
                modfMatched = gotModifiers == 0
            } else {
                if (gotModifiers > 0) {
                    final int and = gotModifiers & neededModifiers;
                    log.info "macthed 3 : ${ks} ${gotModifiers} ${neededModifiers} ${and}"
                    modfMatched = and == gotModifiers
                }
            }
            if (modfMatched) {
                log.info "macthed 4 : ${ks}"
                if (e.getKeyCode() == ks.getKeyCode()) {
//                    log.info "macthed 1 : ${ks} ${e.getID()}"
                    if (e.getID() ==
                            KeyEvent.KEY_PRESSED) {
                        log.info "matched 2 : ${ks}"
                        return true
                    }
                }
            }
        }
        return false;
    }
}
