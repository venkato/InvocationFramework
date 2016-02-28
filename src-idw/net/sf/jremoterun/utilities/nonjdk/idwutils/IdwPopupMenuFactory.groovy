package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.WindowPopupMenuFactory
import net.infonode.docking.util.ViewFactoryManager
import net.infonode.docking.util.WindowMenuUtil
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtilsParent
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField

import javax.swing.*
import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.List
import java.util.logging.Logger

@CompileStatic
class IdwPopupMenuFactory implements WindowPopupMenuFactory {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private final WindowPopupMenuFactory factoryDefault;

    public volatile DockingWindow latestWindow

    public List<JMenuItem> menuItems = []

    IdwPopupMenuFactory(ViewFactoryManager viewMapManager) {
        this.factoryDefault = WindowMenuUtil.createWindowMenuFactory(viewMapManager, true, true);
    }

    @Override
    JPopupMenu createPopupMenu(DockingWindow window) {
        latestWindow = window
        JPopupMenu popupMenu = factoryDefault.createPopupMenu(window)
        menuItems.each { popupMenu.add(it) }
        createMenuItem(popupMenu, IdwShortcuts.rename, window, {
            renameWindow(window);
        });

        createMenuItem(popupMenu, IdwShortcuts.switchSplitLayout, window, {
            IdwActions.switchSpliptWondowLayout(window)
        });

        createMenuItem(popupMenu, IdwShortcuts.swapTabs, window, {
            IdwActions.swapTab(window);
        });

        createMenuItem(popupMenu, IdwShortcuts.moveOppositeTab, window, {
            IdwActions.moveTab(window)
        });


        createMenuItem(popupMenu, IdwShortcuts.moveToDialog, window, {
            IdwMoveToNewTab.showMoveToMenu2(window)
        })

        createMenuItem(popupMenu, IdwShortcuts.maximaze, window,{
            log.info("cp3")
            return IdwActions.maximazeIdw(window)
        });



        return popupMenu
    }


    private void createMenuItem(JPopupMenu popupMenu, IdwShortcuts menuName, DockingWindow window, ActionListener actionListener) {
        KeyStroke shortcut = menuName.keyStroke
        JMenuItem menuItem = new JMenuItem(menuName.displayName)
        popupMenu.add(menuItem)
        menuItem.setAccelerator(shortcut)
        menuItem.addActionListener(actionListener);
    }

    public static void renameWindow(DockingWindow window) {
        JDialog dialog = new JDialog(JrrSwingUtilsParent.findParentWindow(window, Window), "Rename idw");
        JPanel panel = new JPanel(new BorderLayout())
        NameAndTextField nameAndTextField = new NameAndTextField("New name", window.getTitle());
        dialog.getContentPane().add(panel, BorderLayout.CENTER)
        panel.add(nameAndTextField)
        JButton button = new JButton("Rename");
        char mnemonic  = 'R'
        button.setMnemonic(mnemonic)
        panel.add(button, BorderLayout.SOUTH)
        button.addActionListener {
            window.getWindowProperties().setTitleProvider(
                    new MyDockingWindowTitleProvider(nameAndTextField.getText()));
            dialog.dispose()
        }
        nameAndTextField.textField.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        dialog.dispose()
                        break;
                }
            }
        });
        dialog.pack()
        dialog.setVisible(true)

    }
}
