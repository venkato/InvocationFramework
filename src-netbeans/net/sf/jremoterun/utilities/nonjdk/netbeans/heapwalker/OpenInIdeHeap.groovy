package net.sf.jremoterun.utilities.nonjdk.netbeans.heapwalker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.OsInegrationClientI
import org.netbeans.lib.profiler.ui.components.JExtendedTable
import org.netbeans.lib.profiler.ui.components.JTreeTable
import org.netbeans.modules.profiler.heapwalk.ClassesController
import org.netbeans.modules.profiler.heapwalk.ClassesListController
import org.netbeans.modules.profiler.heapwalk.FieldsBrowserController
import org.netbeans.modules.profiler.heapwalk.HeapFragmentWalker
import org.netbeans.modules.profiler.heapwalk.HeapWalker
import org.netbeans.modules.profiler.heapwalk.InstancesController
import org.netbeans.modules.profiler.heapwalk.ReferencesBrowserController
import org.netbeans.modules.profiler.heapwalk.model.BrowserUtils
import org.netbeans.modules.profiler.heapwalk.model.HeapWalkerNode
import org.netbeans.modules.profiler.heapwalk.ui.ClassesListControllerUI
import org.netbeans.modules.profiler.heapwalk.ui.FieldsBrowserControllerUI
import org.netbeans.modules.profiler.heapwalk.ui.HeapWalkerUI
import org.netbeans.modules.profiler.heapwalk.ui.ReferencesBrowserControllerUI
import org.openide.windows.TopComponent

import javax.swing.JFrame
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.tree.TreePath;
import java.util.logging.Logger;

@CompileStatic
class OpenInIdeHeap {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    HeapWalker heapWalker
    OsInegrationClientI inegrationClientI
    HeapFragmentWalker mainHeapWalker
    InstancesController instancesController

    OpenInIdeHeap(HeapWalker heapWalker, OsInegrationClientI inegrationClientI) {
        this.heapWalker = heapWalker
        this.inegrationClientI = inegrationClientI
        mainHeapWalker = heapWalker.getMainHeapWalker();
        instancesController = mainHeapWalker.getInstancesController()
    }

    static HeapWalker createUi(File heapFile) {
        HeapWalker heapWalker = new HeapWalker(heapFile);
        //HeapWalkerUI heapWalkerUI = heapWalker.getTopComponent() as HeapWalkerUI;// new HeapWalkerUI(heapWalker);
        return heapWalker;
    }

    void addAll() {
        addRefSupport();
        addClassSupport()
        addFieldsSupport();
    }


    void addRefSupport() {
        ReferencesBrowserController referencesBrowserController = instancesController.getReferencesBrowserController()
        ReferencesBrowserControllerUI referencesBrowserControllerUI = referencesBrowserController.getPanel() as ReferencesBrowserControllerUI
        JPopupMenu popupMenuFields = JrrClassUtils.getFieldValue(referencesBrowserControllerUI, 'tablePopup') as JPopupMenu
        JMenuItem openClassInIde = new JMenuItem('open field in ide')
        JrrClassUtils.setFieldValue(referencesBrowserControllerUI, 'showSourceItem', openClassInIde)
        openClassInIde.addActionListener {
            JTreeTable fieldsListTable = JrrClassUtils.getFieldValue(referencesBrowserControllerUI, 'fieldsListTable') as JTreeTable
            int row = fieldsListTable.getSelectedRow();
            log.info "field row ${row}"
            if (row != -1) {
                TreePath pathForRow = fieldsListTable.getTree().getPathForRow(row)
                Object lastPathComponent = pathForRow.getLastPathComponent()
                if (lastPathComponent instanceof HeapWalkerNode) {
                    HeapWalkerNode heapWalkerNode = (HeapWalkerNode) lastPathComponent;
                    String fieldName = heapWalkerNode.getName();
                    String clasType = traslateClassName(heapWalkerNode.getType())
                    log.info "opening c=${clasType} f=${fieldName} .."
                    inegrationClientI.openField(clasType, fieldName);
                }
                //HeapWalkerNode node =

            }
        }
        popupMenuFields.add(openClassInIde)

    }

    void addFieldsSupport() {
        FieldsBrowserController fieldsBrowserController = instancesController.getFieldsBrowserController()
        FieldsBrowserControllerUI fieldsBrowserControllerUI = fieldsBrowserController.getPanel() as FieldsBrowserControllerUI
        JPopupMenu popupMenuFields = JrrClassUtils.getFieldValue(fieldsBrowserControllerUI, 'tablePopup') as JPopupMenu
        JMenuItem openClassInIde = new JMenuItem('open field in ide')
//            JrrClassUtils.setFieldValue(fieldsBrowserControllerUI, 'showSourceItem', openClassInIde)
        openClassInIde.addActionListener {
            JTreeTable fieldsListTable = JrrClassUtils.getFieldValue(fieldsBrowserControllerUI, 'fieldsListTable') as JTreeTable
            int row = fieldsListTable.getSelectedRow();
            log.info "field row ${row}"
            if (row != -1) {
                TreePath pathForRow = fieldsListTable.getTree().getPathForRow(row)
//                    IstyStartupUtils.showObjectInIsty(pathForRow)
                Object lastPathComponent = pathForRow.getLastPathComponent()
                if (lastPathComponent instanceof HeapWalkerNode) {
                    HeapWalkerNode heapWalkerNode = (HeapWalkerNode) lastPathComponent;
                    String fieldName = heapWalkerNode.getName();
                    HeapWalkerNode walkerNodeParent = heapWalkerNode.getParent()
                    String clasType = traslateClassName(walkerNodeParent.getType())
                    log.info "opening c=${clasType} f=${fieldName} .."
                    inegrationClientI.openField(clasType, fieldName);
                }
                //HeapWalkerNode node =

            }
        }
        popupMenuFields.add(openClassInIde)
    }

    String traslateClassName(String cl) {
        return cl.replace('$', '.')
    }

    void addClassSupport() {
        ClassesController classesController = mainHeapWalker.getClassesController();
        ClassesListController classesListController = classesController.getClassesListController()
        ClassesListControllerUI classesListControllerUI = classesListController.getPanel() as ClassesListControllerUI
        JPopupMenu popupMenuClasses = JrrClassUtils.getFieldValue(classesListControllerUI, 'tablePopup') as JPopupMenu
        JMenuItem openClassInIde = new JMenuItem('open class in ide')
        JrrClassUtils.setFieldValue(classesListControllerUI, 'showSourceItem', openClassInIde)
        openClassInIde.addActionListener {
            JExtendedTable classesListTable = JrrClassUtils.getFieldValue(classesListControllerUI, 'classesListTable') as JExtendedTable
            int row = classesListTable.getSelectedRow();
            log.info "row = ${row}"
            if (row != -1) {
                Object[][] displayCache = JrrClassUtils.getFieldValue(classesListControllerUI, 'displayCache') as Object[][]
                String className = traslateClassName(BrowserUtils.getArrayBaseType((String) displayCache[row][0]));
                log.info "opening ${className}"
                inegrationClientI.openClass(className)
            }

        }
        popupMenuClasses.add(openClassInIde)
    }


}
