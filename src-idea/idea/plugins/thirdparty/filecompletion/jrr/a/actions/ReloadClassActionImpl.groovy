package idea.plugins.thirdparty.filecompletion.jrr.a.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompileStatusNotification
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl

@CompileStatic
public class ReloadClassActionImpl extends AnAction {

    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            Project project = OSIntegrationIdea.openedProject;
            CompilerManager.getInstance(project).make(new CompileStatusNotification() {

                @Override
                void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                    log.debug "${aborted} ${errors}"
                    if (!aborted && errors == 0) {
                        startActionImpl();
                    }
                }
            });
        } catch (Exception e) {
            log.debug('', e);
            JrrUtilities.showException("Class reload failed", e);
        }
    }

    static String findClassName(PsiFile psiFile1) {
//        String className;
        if (psiFile1 instanceof PsiJavaFile) {
            PsiJavaFile psiFile = (PsiJavaFile) psiFile1;
            log.debug("pack name 2 : " + psiFile.getPackageName());
            log.debug("just name : " + psiFile.getName());
            return psiFile.getPackageName() + "." + psiFile.getName().replace(".java", "").replace(".class", "");
        }
        if (psiFile1 instanceof GroovyFileImpl) {
            GroovyFileImpl psiFile = (GroovyFileImpl) psiFile1;
            log.debug("pack name 2 : " + psiFile.getPackageName());
            log.debug("just name : " + psiFile.getName());
            return psiFile.getPackageName() + "." + psiFile.getName().replace(".groovy", "").replace(".java", "").replace(".class", "");

        }
        log.debug "stange class : ${psiFile1}"
        return null

//        return className
    }

    static void startActionImpl2() {
        Project openedProject = OSIntegrationIdea.getOpenedProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(openedProject);
        Document document = FileEditorManager.getInstance(openedProject).getSelectedTextEditor().getDocument();
        PsiFile psiFile1 = psiDocumentManager.getPsiFile(document);
        String className = findClassName(psiFile1);
        log.debug("class name 2 : " + className);

        if (className != null) {
            Class clazz = ReloadClassActionImpl.getClassLoader().loadClass(className);
            JrrJavassistUtils.reloadClassAndAnonClasses(clazz);
        }

        //JnaBean.jnaBean.reloadClassAndAnonClasses(clazz);
    }

    static void startActionImpl() {
        log.debug("try start");
        try {
            startActionImpl2()
        } catch (Exception e) {
            log.debug('', e);
            JrrUtilities.showException("Class reload", e);
        }
        log.debug("action started");
    }

    @Override
    void update(AnActionEvent e) {
        boolean enable = isEnabled3()
        e.presentation.visible = enable
        e.presentation.enabled = enable
    }

    boolean isEnabled3() {
        if (SimpleJvmTiAgent.instrumentation == null) {
            return false
        }
        Project openedProject = OSIntegrationIdea.getOpenedProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(openedProject);
        Editor editor = FileEditorManager.getInstance(openedProject).getSelectedTextEditor();
        if (editor == null) {
            log.debug "editor is null"
            return false
        }
        Document document = editor.getDocument();
        PsiFile psiFile1 = psiDocumentManager.getPsiFile(document);
        if (psiFile1 instanceof PsiJavaFile) {
            return true
        }
        if (psiFile1 instanceof GroovyFileImpl) {
            return true
        }
        log.debug "stange class : ${psiFile1}"
        return false


    }
}
