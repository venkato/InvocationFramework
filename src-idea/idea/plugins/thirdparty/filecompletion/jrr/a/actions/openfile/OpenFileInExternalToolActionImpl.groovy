package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.tools.Tool
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import javax.swing.JOptionPane
import javax.swing.SwingUtilities

@CompileStatic
class OpenFileInExternalToolActionImpl extends AnAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    @Override
    void actionPerformed(AnActionEvent e) {
        log.debug "running ${e}"
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true)
        if (place.value == null) {
            log.debug "can't find file name"
            return
        }
        File file;
        if (place.parentFilePath == null) {
            file = new File(place.value)
        } else {
            file = new File(place.parentFilePath, place.value);
        }
        log.debug "file : ${file}"
        if (file.exists()) {
            openFile(file, e);
        } else {
            JrrUtilities.showException("${file.name} file not found", new FileNotFoundException(file.absolutePath))
        }
        //runTool(myActionId, e.getDataContext(), e, 0L, null);
    }


    static Tool findTool() {
        Tool tool
        if (SettingsRef.config.openFileTool != null) {
            tool = SettingsRef.config.openFileTool.findTool();
        }
        if (tool != null) {
            return tool
        }
//            if (OpenFileActionSettings.instance.findAllEnabledTools().size() == 0) {
//                ShowSettingsUtil.getInstance().showSettingsDialog(OSIntegrationIdea.openedProject, "External Tools")
//            } else {
//                ShowSettingsUtil.getInstance().showSettingsDialog(OSIntegrationIdea.openedProject, Constants.pluginName)
//            }
        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(null, "Set openFileTool in Library manager tab")
        }

        return null

    }

    private void openFile(File file, AnActionEvent e) {
        Tool tool = OpenFileInExternalToolActionImpl.findTool();
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        log.debug "virtualFile : ${virtualFile}"
        if (virtualFile == null) {
            VirtualFile parent = LocalFileSystem.getInstance().findFileByIoFile(file.parentFile)
            if (parent == null) {
                JrrUtilities.showException("can't find virtual file", new FileNotFoundException("vertual file not found : ${file.absolutePath}"))
                return
            }
            virtualFile = parent.children.find { it.name == file.name }
            log.debug "found child : ${virtualFile}"
            if (virtualFile == null) {
                log.info "refreshing folder ${file.parentFile}"
                parent.refresh(false, false)
                log.info "refresh finished"
                virtualFile = parent.children.find { it.name == file.name }
                if (virtualFile == null) {
                    JrrUtilities.showException("can't find virtual file 2", new FileNotFoundException(file.absolutePath))
                    return
                }
            }
        }
        if (virtualFile != null) {
            MyDataContext myDataContext = new MyDataContext(e.dataContext, virtualFile)
            tool.execute(e, myDataContext, 0L, null);
            log.debug "run action done"

        }
    }

    @Override
    void setInjectedContext(boolean worksInInjected) {
        super.setInjectedContext(worksInInjected)
    }

    @Override
    void update(AnActionEvent e) {
        // log.debug e
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {

        }else {
            FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true)
            boolean iok = place != null

//            log.debug place
            if (iok) {
                log.debug "found file method with path 2 : ${place.value}"
            } else {
//            log.info "place not found"
            }

            e.presentation.visible = iok
            e.presentation.enabled = iok
            //super.update(e)
        }
    }


}
