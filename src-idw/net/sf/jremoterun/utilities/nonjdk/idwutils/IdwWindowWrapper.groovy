package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.DockingWindow
import net.infonode.docking.View
import net.infonode.docking.title.DockingWindowTitleProvider
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import java.util.logging.Logger

@CompileStatic
class IdwWindowWrapper {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DockingWindow dockingWindow;
    String title;
    int index = -1
    DefaultMutableTreeNode treeNode
    List<IdwWindowWrapper> children = []

//    IdwWindowWrapper(DockingWindow dockingWindow, String title, int index) {
//        this(dockingWindow,title)
//        this.index = index
//    }

    IdwWindowWrapper(DockingWindow dockingWindow) {
        this.dockingWindow = dockingWindow
    }

    IdwWindowWrapper(DockingWindow dockingWindow, String title) {
        this.dockingWindow = dockingWindow
        this.title = title
    }

    void build(DefaultTreeModel model) {
        children.each { it.build(model) }
        DockingWindow dockingWindow2 = dockingWindow
//        int index = -1;
        if (true) {
            DockingWindow parent = dockingWindow2.getWindowParent();
//            if (parent instanceof TabWindow) {
//                TabWindow tabWindow = (TabWindow) parent;
            index = parent.getChildWindowIndex(dockingWindow2)
//            }
        }
        if (dockingWindow2 instanceof View) {
            title = dockingWindow2.getTitle();
        } else {
            DockingWindowTitleProvider provider = dockingWindow2.getWindowProperties().getTitleProvider()
            if (provider instanceof MyDockingWindowTitleProvider) {
                MyDockingWindowTitleProvider myDockingWindowTitleProvider = (MyDockingWindowTitleProvider) provider;
                title = myDockingWindowTitleProvider.getTitle(null)

            } else {
                title = dockingWindow.class.simpleName
            }
        }
        treeNode = new DefaultMutableTreeNode(this);
        children.each { model.insertNodeInto(it.treeNode, treeNode, treeNode.getChildCount()) }
    }

    @Override
    String toString() {
        if (index == -1) {
            return title;
        }
        return "${title} ${index}"
    }
}
