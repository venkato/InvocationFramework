package idea.plugins.thirdparty.filecompletion.jrr.a.charset

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteral
import com.intellij.psi.impl.compiled.ClsMethodImpl
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

import java.nio.charset.Charset

@CompileStatic
public class MyAcceptCharsetProviderImpl implements ElementPattern<PsiElement> {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    private void testNotUsed() {
        Charset.forName('')
    }

    @Override
     boolean accepts(@Nullable Object o) {
        //log.debug "timezone ${o}"
        if (o instanceof com.intellij.psi.impl.source.tree.LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            JavassistCompletionBean element = isOkPsiElement(o);
            log.debug "cp3 ${element}"
            return element != null
        }
        return false;
    }

    static JavassistCompletionBean isOkPsiElement(LeafPsiElement leafPsiElement) {
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            return null;
        }
        JavassistCompletionBean completionBean = new JavassistCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.parent;
        if (!(parent1 instanceof GrArgumentList)) {
            return null;
        }
        completionBean.args = parent1
        if (parent1.getAllArguments().length != 1) {
            return null;
        }
        PsiElement parent2 = parent1.parent
        if (!(parent2 instanceof GrMethodCall)) {
            return null;
        }
        PsiElement child = parent2.children[0];
        if (!(child instanceof GrReferenceExpression)) {
            return null;
        }
        GrExpression expression = parent2.getInvokedExpression()
        if (!(expression instanceof GrReferenceExpression)) {
            return null
        }
        GroovyResolveResult[] variants = expression.getSameNameVariants()
        if (variants.length != 1) {
            log.debug "bad lenth"
            return null
        }
        PsiElement element623 = variants[0].element
        if (!(element623 instanceof ClsMethodImpl)) {
            return null;
        }
        if (element623.getMirror() == null) {
            log.debug "bad static method"
            return null
        }
        if (element623.getName() != 'forName') {
            log.debug "not format"
            return null
        }
        if (element623.getContainingClass().name == 'Charset') {
            return completionBean;
        }

        return null;

    }


    @Override
     boolean accepts(@Nullable Object o, ProcessingContext context) {
        return accepts(o);
    }

    @Override
     ElementPatternCondition<PsiElement> getCondition() {
        log.debug(1)
        return new ElementPatternCondition(null);
    }

}
