package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.jrrbean.JrrBeanMaker

@CompileStatic
class JrrLibMoveFileBean {

    public static JrrLibMoveFileBean bean = JrrBeanMaker
            .makeBeanAndRegisterMBeanNoEx(JrrLibMoveFileBean);


    File file;

    PsiElement wholeFileDeclaration
    File fileDocument
}
