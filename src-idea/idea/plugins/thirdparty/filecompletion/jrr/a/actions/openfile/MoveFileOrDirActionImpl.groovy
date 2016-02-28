package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class MoveFileOrDirActionImpl extends AnAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    @Override
    void actionPerformed(AnActionEvent e) {
        log.debug "running ${e}"
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e,false)
        Editor editor = IdeaOpenFileUtils.findEditor(e)
        if(editor==null){
            log.info "editor is null"
            return
        }
        Document document = editor.getDocument()
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document)
        File fileDocument =  virtualFile.canonicalPath as File
        if(place.value==null){
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
            moveFile(file, place,fileDocument);
        } else {
            JrrUtilities.showException("${file.name} file not found", new FileNotFoundException(file.absolutePath))
        }
    }

    private void moveFile(File file, FileCompletionBean place,File fileDocument ) {
        try {
            MoveFileDialog moveFileDialog = new MoveFileDialog(file, place)
            moveFileDialog.fileDocument = fileDocument
            JrrLibMoveFileBean.bean.fileDocument = fileDocument
            moveFileDialog.dialog.pack()
            moveFileDialog.dialog.setVisible(true)
        }catch (Throwable e){
            JrrUtilities.showException("failed create dialog",e)
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
            return;
        }
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e,false)
        boolean iok = place != null
        if (iok) {
            log.debug place.value
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        //super.update(e)
    }

    private void notUsed(){
        File fsdfsfd= "" as File;
    }


}
