package idea.plugins.thirdparty.filecompletion.jrr.a.actions

import com.intellij.debugger.impl.DebuggerUtilsEx
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.util.text.CharArrayUtil
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.jrrlauncher.JrrJmxRmiLauncher
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.GroovyFileType

import java.text.SimpleDateFormat

@CompileStatic
class RemoteRunActionImpl extends AnAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);
    Date lastAnalized;
    int offset;


    @Override
    void actionPerformed(AnActionEvent e) {
        log.debug e
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        PsiMethod psiMethod = getPlace(e)
        if (psiMethod == null) {
            log.debug "method is null"
            return
        }
        final PsiFile containingFile = psiMethod.getContainingFile();
        if (containingFile == null) {
            log.debug "can't find file"
            return
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
        int lineNumber = document.getLineNumber(psiMethod.textOffset)
        PsiClass clazz = psiMethod.containingClass
        File userHome = System.getProperty('user.home') as File
        if (!userHome.exists()) {
            log.error("userhome not exits : ${userHome}")
            return
        }
        final File file = new File(
                userHome, "jrr.properties");
        file.text = """
# generated at ${new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())}
className=${clazz.qualifiedName}
methodName=${psiMethod.name}
lineNumer=${lineNumber + 2}
"""
        log.debug "file create"
        new OSIntegrationIdea().runLaunchConfiguration('JrrRun', null)
    }

    @Override
    void setInjectedContext(boolean worksInInjected) {
        super.setInjectedContext(worksInInjected)
    }

    @Override
    void update(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        PsiMethod place = getPlace(e)
        boolean iok = place != null
        if (iok) {
//            log.debug place
            iok = plMethod(place)
            if (iok) {
                log.debug "found rrun method : ${place.name}"
            }
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        if (iok) {
            e.presentation.text = "rrun ${place.name}"
        }
        //super.update(e)
    }

    boolean plMethod(PsiMethod psiMethod) {
        String methodName = psiMethod.name
//        log.debug methodName
        if (!methodName.startsWith('rrun')) {
            return false
        }
        PsiClassType find = psiMethod.containingClass.extendsListTypes.find {
            return it.className != null && it.className.contains(JrrJmxRmiLauncher.simpleName)
        };
        return find != null
        //psiMethod.
    }


    @Nullable
    private static PsiMethod getPlace(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

//        PsiElement method ;
//        Document document ;

        if (ActionPlaces.PROJECT_VIEW_POPUP.equals(event.getPlace()) ||
                ActionPlaces.STRUCTURE_VIEW_POPUP.equals(event.getPlace()) ||
                ActionPlaces.FAVORITES_VIEW_POPUP.equals(event.getPlace()) ||
                ActionPlaces.NAVIGATION_BAR_POPUP.equals(event.getPlace())) {
            final PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
            if (psiElement instanceof PsiMethod) {
                return psiElement
                //   log.debug "method found 1 : ${psiMethod.name}"
//                final PsiFile containingFile = psiElement.getContainingFile();
//                if (containingFile != null) {
//                    method = psiElement;
//                    document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
//                }
            }
            return null
        }
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        }
        if (editor == null) {
            return null
        }
        Document document = editor.getDocument();
//                log.debug "editor found "
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) {
            return null
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        FileType fileType = virtualFile != null ? virtualFile.getFileType() : null;
//        PsiMethod psiMethod
        if (StdFileTypes.JAVA == fileType || StdFileTypes.CLASS == fileType) {
            return findMethod(project, editor);
//                        method = psiMethod
            // log.debug "method found 2 : ${psiMethod.name}"
        }
        if (GroovyFileType.GROOVY_FILE_TYPE == fileType) {
            return findMethod(project, editor);
//                            method = psiMethod
            //     log.debug "method found 3 : ${psiMethod.name}"

        }
        //log.debug "bad file type : ${fileType}"


        return null;
    }

    @Nullable
    private static PsiMethod findMethod(Project project, Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }
        final int offset = CharArrayUtil.shiftForward(editor.getDocument().getCharsSequence(), editor.getCaretModel().getOffset(), " \t");
        return DebuggerUtilsEx.findPsiMethod(psiFile, offset);
    }
}
