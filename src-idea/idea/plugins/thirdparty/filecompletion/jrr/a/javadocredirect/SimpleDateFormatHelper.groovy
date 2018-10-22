package idea.plugins.thirdparty.filecompletion.jrr.a.javadocredirect

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrGdkMethod

import java.text.SimpleDateFormat
import java.util.regex.Pattern

@CompileStatic
public class SimpleDateFormatHelper {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    public static boolean isOkPsiElement(LeafPsiElement leafPsiElement) {
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
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
        PsiElement parent2 = parent1.parent
        if (parent2 instanceof PsiNewExpression) {
            PsiType type = parent2.getType();
            if (!(type instanceof PsiClassType)) {
                log.debug "not a type"
                return false;
            }
            PsiClass psiClass = type.resolve();
            if (psiClass == null || !(psiClass.name.contains('SimpleDateFormat'))) {
                log.debug "not a file"
                return false;
            }
            return true
        } else if (parent2 instanceof GrNewExpression) {
            PsiType type = parent2.getType();
            if (!(type instanceof PsiClassType)) {
                log.debug "not a type"
                return false;
            }
            PsiClass psiClass = type.resolve();
            if (psiClass == null || !(psiClass.name.contains('SimpleDateFormat'))) {
                log.debug "not a file"
                return false;
            }
            return true
        }

        if (!(parent2 instanceof GrMethodCall)) {
            log.debug "not method"
            return false;
        }
        GrExpression expression = parent2.getInvokedExpression()
        if (!(expression instanceof GrReferenceExpression)) {
            return false
        }
        GroovyResolveResult[] variants = expression.getSameNameVariants()
        if (variants.length != 1) {
            log.debug "bad lenth"
            return false
        }
        PsiElement element623 = variants[0].element
        if (!(element623 instanceof GrGdkMethod)) {
            return false
        }
        PsiMethod staticMethod = element623.getStaticMethod()
        if (staticMethod == null) {
            log.debug "bad static staticMethod"
            return false
        }
        if (staticMethod.name != 'format') {
            log.debug "not format"
            return false
        }
        return staticMethod.containingClass.name == 'DateGroovyMethods'


    }

    private void test() {
        Date date = null
        new SimpleDateFormat('yyyy-MM')
        //new Date().format('yyyy-MM')
        //date.format('yyyy-MM')
    }


    private void testNotUsed() {
        Date date = null
        Calendar calendar = null
        new SimpleDateFormat('yyyy-MM')
        //calendar.format('yyyy-MM')
        //new Date().format('yyyy-MM')
        //new Date().format('yyyy-MM', TimeZone.getTimeZone('Etc/GMT+3'))
        //date.format('yyyy-MM')
        Pattern p = ~"dsfsdfsd"
    }


}
