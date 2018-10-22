package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.View
import net.infonode.docking.WindowBar
import net.infonode.docking.title.DockingWindowTitleProvider
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtils

import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import java.awt.*
import java.awt.event.*
import java.util.List
import java.util.logging.Logger

@CompileStatic
class IdwWindowFinder implements KeyEventDispatcher {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JPanel panel = new JPanel(new BorderLayout())

    public JTextField textField = new JTextField();
    public View view = new View("Idw window finder", null, panel)
//    JButton refresh = new JButton("refresh")


    public JTree treeSwing = new JTree()
    public JScrollPane scrollPane = new JScrollPane(treeSwing)
    static Exception creationStack


    public static
    volatile KeyStroke awtMonitorKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.ALT_MASK);

    IdwWindowFinder() {
        if (creationStack != null) {
            log.info("creation first 1", creationStack)
            log.info("creation current", new Exception())
            throw new Exception("was created before")
        }
        JrrSwingUtils.tranferFocus(view, textField)
        creationStack = new Exception()
        panel.add(textField, BorderLayout.NORTH)
        panel.add(scrollPane, BorderLayout.CENTER)
        treeSwing.rootVisible = false
        IdwUtils.setClosable(view, false)
        panel.addFocusListener(new FocusAdapter() {
            @Override
            void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater {
                    log.info "clearing last focus : ${lastSelectedWindow}"
                    lastSelectedWindow = null

                }
            }
        })
        textField.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        hidePanel();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        treeSwing.requestFocus()
                        treeSwing.requestFocusInWindow()
                        break;
                    case KeyEvent.VK_ENTER:
                        List<IdwWindowWrapper> windows = findMatchedWindows();
                        if (windows.size() == 1) {
                            IdwWindowWrapper windowWrapper = (IdwWindowWrapper) windows.first()
                            selectWindow(windowWrapper.dockingWindow)
                        } else {
                            log.info "found window count != 1 : ${windows.size()}"
                        }
                        break;
                    default:
                        refreshTree()
                }
            }
        })
        treeSwing.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    processSelection()
                }
            }
        })
        treeSwing.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case { code == KeyEvent.VK_ESCAPE }:
                        hidePanel();
                        if (lastSelectedWindow != null) {
                            IdwUtils.setVisible(lastSelectedWindow);
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        processSelection()
                        break
                    case { KeyEvent.VK_0 <= code && code <= KeyEvent.VK_9 }:
                    case { KeyEvent.VK_A <= code && code <= KeyEvent.VK_Z }:
                        textField.requestFocus()
                        textField.requestFocusInWindow()
                        break
                }
            }
        })
        if (IdwUtilsStarter.rootWindow == null) {
            log.info "root window is null"
        } else {
            SwingUtilities.invokeLater { refreshTree() }
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        treeSwing.setScrollsOnExpand(true)

    }

    void hidePanel() {
        DockingWindow parent = view.getWindowParent()
        if (parent instanceof WindowBar) {
            WindowBar windowBar = (WindowBar) parent;
            windowBar.setSelectedTab(-1)
        }
    }

    volatile DockingWindow lastSelectedWindow;

    @Override
    public boolean dispatchKeyEvent(final KeyEvent e) {
        try {
            final int keyModifiers = e.getModifiers();
            final KeyStroke awtMonitorKeyStroke1 = awtMonitorKeyStroke;
            final int and = keyModifiers & awtMonitorKeyStroke1.getModifiers();
            if ((e.getID() == awtMonitorKeyStroke1.getKeyEventType()) && and == keyModifiers && and != 0
                    && (e.getKeyCode() == awtMonitorKeyStroke1.getKeyCode())) {
                lastSelectedWindow = IdwUtils.findLastFocusedChildDeep(IdwUtilsStarter.rootWindow)
                IdwUtils.setVisible(view)
                refreshTree();
                textField.requestFocus()
                textField.requestFocusInWindow()
                textField.selectAll()
                return true
            }
        } catch (Throwable e2) {
            log.info("${e}", e2);
            return false;
        }
        return false;
    }

    List<IdwWindowWrapper> findMatchedWindows() {
        Collection<DockingWindow> windows = IdwUtils.getChildrenDeep(IdwUtilsStarter.rootWindow)
        List<IdwWindowWrapper> all = windows.collect { createWrapperForWindow(it) }.findAll { it != null }
        String text = textField.getText().toLowerCase()
        if (text.length() > 0) {
            all = all.findAll { it.title.toLowerCase().contains(text) }
        }
        return all
    }


    void refreshTree() {
        DefaultTreeModel model = treeSwing.getModel() as DefaultTreeModel;
        DefaultMutableTreeNode root = model.root as DefaultMutableTreeNode;

        List childs = new ArrayList((List)root.children().toList())
        childs.each {
            root.remove(it as DefaultMutableTreeNode)
//            model.removeNodeFromParent(it);
        }
        assert root.getChildCount() == 0
        List<IdwWindowWrapper> all = findMatchedWindows()
        log.info "found matched : ${all.size()}"
        buildTree2(all,model,root)
        model.reload()
        SwingUtilities.invokeLater {
            int count = treeSwing.getRowCount()
//            log.info "count = ${count}"
            for (int i = 0; i < treeSwing.getRowCount(); i++) {
                treeSwing.expandRow(i);
            }
        }
    }


    static void buildTree2(List<IdwWindowWrapper> all,DefaultTreeModel model,DefaultMutableTreeNode root) {
        List<IdwWindowWrapper> tree = buildTree(all)
        tree = tree.findAll {
            IdwUtils.isTopLevelWindow(it.dockingWindow)
        }
        log.info "tree found : ${all.size()}"
        tree.each {
            it.build(model)
            model.insertNodeInto(it.treeNode, root, root.getChildCount())
        }
    }

    static List<IdwWindowWrapper> buildTree(List<IdwWindowWrapper> all) {
        List<IdwWindowWrapper> result = [];
        //List<IdwWindowWrapper> result = new IdentityHashMap<>()
        //result.addAll(all)
        //        boolean childFound = false
        all.each { el ->
            IdwWindowWrapper idw = result.find { it.dockingWindow == el.dockingWindow }
            if (idw == null) {
                result.add(el)
                if (!IdwUtils.isTopLevelWindow(el.dockingWindow)) {
                    addParent(el.dockingWindow.getWindowParent(), result).children.add(el)
                }
            }
        }
        return result
    }

    static IdwWindowWrapper addParent(DockingWindow thi, List<IdwWindowWrapper> result) {
        IdwWindowWrapper idw = result.find { it.dockingWindow == thi }
        if (idw == null) {
            idw = new IdwWindowWrapper(thi)
            result.add(idw)
        } else {
            return idw
        }
        if (!IdwUtils.isTopLevelWindow(thi)) {
            DockingWindow parent = thi.getWindowParent()
            addParent(parent, result).children.add(idw)
        }
        return idw
    }

//    void returnOnlyTop()

//    void addWindow(List<IdwWindowWrapper> list, IdwWindowWrapper dockingWindow) {
//        IdwWindowWrapper wrapper = list.find { it.dockingWindow == dockingWindow.dockingWindow }
//        if (wrapper == null) {
//            list.add(dockingWindow);
//        } else {
//            wrapper.children.add(dockingWindow);
//        }
//    }


    void processSelection() {
        TreePath path = treeSwing.getSelectionPath();
        DefaultMutableTreeNode node = path.getPathComponent(path.getPathCount() - 1) as DefaultMutableTreeNode;
        IdwWindowWrapper selected = node.getUserObject() as IdwWindowWrapper;
        if (selected == null) {
            log.info "no selction"
        } else {
            selectWindow(selected.dockingWindow)
        }
    }

    private selectWindow(DockingWindow dockingWindow) {
        hidePanel();
        lastSelectedWindow = null
        IdwUtils.setVisible(dockingWindow)
        dockingWindow.requestFocus()
        dockingWindow.requestFocusInWindow()

    }

    IdwWindowWrapper createWrapperForWindow(DockingWindow it) {
        int index = -1;
        if (true) {
            DockingWindow parent = it.getWindowParent();
            if (parent instanceof TabWindow) {
                TabWindow tabWindow = (TabWindow) parent;
                index = parent.getChildWindowIndex(it)
            }
        }
        IdwWindowWrapper idwWindowWrapper
        if (it instanceof View) {
            idwWindowWrapper = new IdwWindowWrapper(it, it.getTitle());
        } else {
            DockingWindowTitleProvider provider = it.getWindowProperties().getTitleProvider()
            if (provider instanceof MyDockingWindowTitleProvider) {
                MyDockingWindowTitleProvider myDockingWindowTitleProvider = (MyDockingWindowTitleProvider) provider;
                String title = myDockingWindowTitleProvider.getTitle(null)
                idwWindowWrapper = new IdwWindowWrapper(it, title)
//                log.info("found by title provide : ${title}")
            }
        }
        return idwWindowWrapper;
    }

}
