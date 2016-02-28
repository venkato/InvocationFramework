package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic

import javax.swing.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

@CompileStatic
enum IdwShortcuts  implements Shortcuts {


    nextUpperTab("Next upper tab", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK)),

    nextTab("Next tab", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_MASK)),

    prevoiusUpperTab("Previous upper tab", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK)),

    prevoiusTab("Prevoius tab", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_MASK)),

    focusOppositeTab("Focus opposite tab", KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK)),

    switchSplitLayout("Switch Horizontival / Vertical", KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_MASK)),

    swapTabs("Swap tabs", KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_MASK)),

    moveOppositeTab("Move tab to opposite SplitWindow tab", KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_MASK)),

    moveToDialog("Move to ..", KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.ALT_MASK)),

    rename("Rename ..", KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.SHIFT_MASK)),


    maximaze("Maximize", KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.CTRL_MASK))


    String displayName;
    KeyStroke keyStroke;

    IdwShortcuts(String name, KeyStroke ks) {
        this.displayName = name;
        this.keyStroke = ks;
    }

}
