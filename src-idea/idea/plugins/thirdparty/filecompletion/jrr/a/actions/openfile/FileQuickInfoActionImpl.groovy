package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import javax.swing.JDialog
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

@CompileStatic
class FileQuickInfoActionImpl extends AnAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


//    static AddFilesToClassLoaderGroovy addFilesToClassLoader = new AddFilesToClassLoaderGroovy() {
//        @Override
//        void addFileImpl(File file) throws Exception {
//
//        }
//    }


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
        openFile(file, e);
        //runTool(myActionId, e.getDataContext(), e, 0L, null);
    }


    private void openFile(File file, AnActionEvent e) {
        InputEvent event = e.inputEvent
//        List l = [e,event,e.presentation]

        SwingUtilities.invokeLater {
            JDialog dialog = new JDialog((Frame) null, "File info")
            String text = file.getAbsolutePath().replace('\\', '/');
            JTextArea textArea = new JTextArea(text)
            dialog.getContentPane().add(textArea, BorderLayout.CENTER)
            dialog.setLocation(200, 300)
            dialog.setSize(300, 100)
            dialog.setVisible(true)
            dialog.toFront()
            textArea.requestFocusInWindow()
            textArea.requestFocus()
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        log.info "disposing dialog"
                        dialog.dispose()
                    }
                }
            })
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
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true)
        boolean iok = place != null
        if (iok) {
//            log.debug place
            if (iok) {
                log.debug "found file method with path : ${place.value}"
            }
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        //super.update(e)
    }

/*
    @Nullable
    static FileCompletionBean getPlace(AnActionEvent event, boolean searchForMavenId) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        PsiElement psiElement1 = null;
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        }
        if (editor != null) {
            Document document = editor.getDocument();
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (file != null) {
                final VirtualFile virtualFile = file.getVirtualFile();
                FileType fileType = virtualFile != null ? virtualFile.getFileType() : null;
                psiElement1 = OpenFileInExternalToolActionImpl.findMethod(project, editor);
//                log.debug "psiElement1 ${psiElement1?.class.name} ${psiElement1}"
            } else {
            }
        }
        if (psiElement1 instanceof PsiJavaToken) {
            log.debug "cp3"
            return MyAcceptFileProviderImpl.isOkJavaPsiElement((PsiJavaToken) psiElement1)
        }
        if (!(psiElement1 instanceof LeafPsiElement)) {
            return null
        }
        if (searchForMavenId && MyAcceptMavenProviderImpl.isOkPsiElement(psiElement1)) {
            String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(psiElement1.parent);
            List<String> ids = value4.tokenize(':')
            if (value4.count(':') != 2 || ids.size() != 3) {
                log.debug "Not a maven token ${value4}"
                return null
            }
            MavenId mavenId = new MavenId(value4)
            File file = addFilesToClassLoader.mavenCommonUtils.findMavenOrGradle(mavenId)
            if (file == null) {
                log.info "Maven token not found : ${value4}"
                return null
            }
            FileCompletionBean element = new FileCompletionBean()
            element.value = file.absolutePath
            return element
        }

        log.debug "cp4"
        FileCompletionBean element = MyAcceptFileProviderImpl.isOkJavaAndGroovyPsiElement((LeafPsiElement) psiElement1, true)
        if (element == null) {
            log.debug "cp2 ${psiElement1}"
            File element99 = OpenFileInExternalToolActionImpl.isVar(psiElement1);
            if (element99 == null) {
                log.debug "cp3 not found ${psiElement1}"
                return null
            }
            FileCompletionBean completionBean = new FileCompletionBean()
            completionBean.value = element99.name
            completionBean.parentFilePath = element99.parentFile
            return completionBean;
        } else {
            return element
        }

    }
/*

    private static File isVar(PsiElement psiElement) {
        if (!(psiElement instanceof LeafPsiElement)) {
            return null;
        }
        log.debug "cp 1"
        PsiElement parent23 = psiElement.parent
        if (parent23 instanceof GrReferenceExpression) {
            GrReferenceExpression e = (GrReferenceExpression) parent23;
            log.debug "cp 2"
            PsiType typeParent = e.type
            if (typeParent instanceof PsiClassType) {
                log.debug "cp 3"
//                    PsiClassType psitype = (PsiClassType) typeParent;
                PsiClass resolve = typeParent.resolve()

                if (resolve == null || !(resolve.name.contains('File'))) {
                    log.debug "no a file"
                    return null
                }
                log.debug("accpted")
                if (!(e.sameNameVariants?.length == 1)) {
                    log.debug "args not 1"
                    return null;
                }
                PsiElement varRef = e.sameNameVariants[0].element;
                File var3 = MyAcceptFileProviderImpl.findFileFromVarGeneric(varRef)
                log.debug "accteped : ${var3}"
                return var3

            }
            return null
        }
        File file1 = MyAcceptFileProviderImpl.findFileFromVarGeneric(parent23)
        log.debug "found : ${file1} ${parent23}"
        return file1


    }

    @Nullable
    private static PsiElement findMethod(Project project, Editor editor) {
        if (editor == null) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }
        final int offset = CharArrayUtil.shiftForward(editor.getDocument().getCharsSequence(), editor.getCaretModel().getOffset(), " \t");
        return psiFile.findElementAt(offset)
    }
*/

}
