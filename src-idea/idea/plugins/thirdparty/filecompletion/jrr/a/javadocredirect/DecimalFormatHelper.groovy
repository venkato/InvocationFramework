package idea.plugins.thirdparty.filecompletion.jrr.a.javadocredirect

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression

import java.text.DecimalFormat

@CompileStatic
public class DecimalFormatHelper {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    static boolean isOkPsiElement(LeafPsiElement leafPsiElement) {
        log.debug "is ok ? ${leafPsiElement}"
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            log.debug "not psi"
            return false;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            log.debug "not a string"
            return false;
        }
        JavassistCompletionBean completionBean = new JavassistCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.parent;
        if (!(parent1 instanceof PsiExpressionList)) {
            log.debug "not gr args"
            return false;
        }
        completionBean.args = parent1
        PsiElement parent2 = parent1.getParent()
        if (parent2 instanceof PsiNewExpression) {
            PsiType type = parent2.getType();
            if (!(type instanceof PsiClassType)) {
                log.debug "not a type"
                return false;
            }
            PsiClass psiClass = type.resolve();
            if (psiClass == null || !(psiClass.name.contains('DecimalFormat'))) {
                log.debug "not a file"
                return false;
            }
            return true
        }
        if (!(parent2 instanceof GrNewExpression)) {
            log.debug "not not"
            return false;
        }
        PsiType type = parent2.getType();
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return false;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || !(psiClass.name.contains('DecimalFormat'))) {
            log.debug "not a file"
            return false;
        }
        return true


    }


    private void testNotUsed() {
        new DecimalFormat("00")
    }
}
