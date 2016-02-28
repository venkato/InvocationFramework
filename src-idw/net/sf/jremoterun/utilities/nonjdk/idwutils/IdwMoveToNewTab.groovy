package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.TabWindow
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtilsParent

import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.List
import java.util.logging.Logger

@CompileStatic
class IdwMoveToNewTab {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    //public JPanel panel = new JPanel(new BorderLayout())


    IdwMoveToNewTab() {
    }

    static void showMoveToMenu2(DockingWindow dw) {
        IdwMoveToNewTab.showMoveToMenu(dw, new NewValueListener<TabWindow>() {
            @Override
            void newValue(TabWindow tabWindow) {
                tabWindow.addTab(dw)
                SwingUtilities.invokeLater{
                    IdwUtils.setVisible(dw)
                }
            }
        })
    }



    static void showMoveToMenu(DockingWindow exclude, NewValueListener<TabWindow> newValue) {
        JTree treeSwing = new JTree(new Object[0])
        DefaultTreeModel model = treeSwing.getModel() as DefaultTreeModel;
        DefaultMutableTreeNode root = model.root as DefaultMutableTreeNode;
        treeSwing.setRootVisible(false)
        List<IdwWindowWrapper> all = findMatchedWindows(exclude)
        log.info "found matched : ${all.size()}"
        IdwWindowFinder.buildTree2(all, model, root)
        model.reload()
        int count = treeSwing.getRowCount()
//            log.info "count = ${count}"
        for (int i = 0; i < treeSwing.getRowCount(); i++) {
            treeSwing.expandRow(i);
        }
        JDialog dialog = new JDialog(JrrSwingUtilsParent.findParentWindow(exclude, Window), "Move to")
        Runnable handleSelection = {
            try {
                TreePath path = treeSwing.getSelectionPath();
                DefaultMutableTreeNode node = path.getPathComponent(path.getPathCount() - 1) as DefaultMutableTreeNode;
                IdwWindowWrapper selected = node.getUserObject() as IdwWindowWrapper;
                if (selected == null) {
                    log.info "no selction"
                } else {
                    if ((selected.dockingWindow instanceof TabWindow)) {
                        TabWindow tabWindow4 = (TabWindow) selected.dockingWindow;
                        dialog.dispose();
                        newValue.newValue(tabWindow4)
                    } else {
                        log.info "Not at TabWindow : ${selected.dockingWindow}"

                    }
                }
            } catch (Throwable e3) {
                JrrUtilities.showException("Failed move", e3)
            }
        }
        treeSwing.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_ESCAPE:
                        dialog.dispose()
                        break
                    case KeyEvent.VK_ENTER:
                        handleSelection.run()
                        break
                }
            }
        })
        treeSwing.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    handleSelection.run()
                }
            }
        })

        dialog.getContentPane().add(treeSwing)
        dialog.pack()
        dialog.setVisible(true)
        SwingUtilities.invokeLater {
            dialog.toFront()
            treeSwing.requestFocusInWindow()
            treeSwing.requestFocus()
        }

    }

    static List<IdwWindowWrapper> findMatchedWindows(DockingWindow exclude) {
        List<DockingWindow> exclude3 = []
        if (exclude != null) {
            exclude3.add(exclude)
            exclude3.addAll(IdwUtils.getChildrenDeep(exclude))
            exclude3.add IdwUtils.getParentIdwWindowSpecial(exclude,TabWindow)
        }
        List<TabWindow> windows = IdwUtils.getChildrenDeep(IdwUtilsStarter.rootWindow).findAll {
            it instanceof TabWindow
        }.findAll { !exclude3.contains(it) } as List<TabWindow>
        List<IdwWindowWrapper> all = windows.collect { new IdwWindowWrapper(it) };
//        all = IdwWindowFinder.buildTree(all)
        return all
    }


}
