package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.text.CharArrayUtil
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.MyAcceptMavenProviderImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import org.apache.log4j.LogManager
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

@CompileStatic
class IdeaOpenFileUtils {

    private static final org.apache.log4j.Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    static MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()

    static FileCompletionBean getPlace(AnActionEvent event, boolean searchForMavenId) {
        PsiElement psiElement1 = getPlace3(event);
        if (psiElement1 == null) {
            return null
        }
        FileCompletionBean place2 = getPlace2(psiElement1, searchForMavenId)
//        log.info "a pace2 : ${place2!=null}"
        return place2
    }

    @Nullable
    static Editor findEditor(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        }
        return editor

    }

    private static PsiElement getPlace3(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        Editor editor = findEditor(event)
        if (editor == null) {
//            log.info "a editor is null"
            return null
        }
        Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) {
//            log.info "a file is null"
            return null
        }
//                final VirtualFile virtualFile = file.getVirtualFile();
//                FileType fileType = virtualFile != null ? virtualFile.getFileType() : null;
        PsiElement psiElement = findMethod(project, document, editor);
//                log.debug "psiElement1 ${psiElement1?.class.name} ${psiElement1}"
        log.info "a psi el found : ${psiElement!=null}"
        return psiElement
    }


    private static FileCompletionBean getPlace2(PsiElement psiElement1, boolean searchForMavenId) {

//        if (psiElement1 instanceof PsiJavaToken) {
//            log.debug "cp3"
//            return MyAcceptFileProviderImpl.isOkJavaPsiElement((PsiJavaToken) psiElement1,true)
//        }
        if (!(psiElement1 instanceof LeafPsiElement)) {
//            log.info "a not LeafPsiElement"
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
            File file = mavenCommonUtils.findMavenOrGradle(mavenId)
            if (file == null) {
                log.info "Maven token not found : ${value4}"
                return null
            }
            FileCompletionBean element = new FileCompletionBean()
            element.value = file.absolutePath
            return element
        }

        log.debug "cp4"
        FileCompletionBean element = MyAcceptFileProviderImpl.isOkJavaAndGroovyPsiElement(psiElement1)
        if (element != null) {
            return element
        }
        log.debug "cp2 ${psiElement1}"
        File element99 = isVar(psiElement1);
        if (element99 == null) {
            log.debug "cp3 not found ${psiElement1}"
            return null
        }
        FileCompletionBean completionBean = new FileCompletionBean()
        completionBean.value = element99.name
        completionBean.parentFilePath = element99.parentFile
        return completionBean;
    }

    private static File isVar(PsiElement psiElement) {
        if (!(psiElement instanceof LeafPsiElement)) {
            return null
        }
        log.debug "cp 1"
        PsiElement parent3 = psiElement.parent
        if (parent3 instanceof GrReferenceExpression) {
            log.debug "cp 2"
            GrReferenceExpression e = (GrReferenceExpression) parent3;
            PsiType type = e.type;
            if (type instanceof PsiClassType) {
                log.debug "cp 3"
                PsiClass resolve = type.resolve()
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
        } else {
            File file1 = MyAcceptFileProviderImpl.findFileFromVarGeneric(parent3)
            log.debug "found : ${file1} ${parent3}"
            return file1

        }


    }

    @Nullable
    private static PsiElement findMethod(Project project, Document document,Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) {
//            log.info "a psi file found"
            return null;
        }
        final int offset = CharArrayUtil.shiftForward(document.getCharsSequence(), editor.getCaretModel().getOffset(), " \t");
        return psiFile.findElementAt(offset)
    }
}
