package idea.plugins.thirdparty.filecompletion.jrr.a.javassist

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

@CompileStatic
public class MyAcceptJavassistProviderImpl implements ElementPattern<PsiElement> {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    @Override
    public boolean accepts(@Nullable Object o) {
        if (o instanceof com.intellij.psi.impl.source.tree.LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;

            boolean accepted = isOkPsiElement((LeafPsiElement) o) != null;
            log.debug "${accepted} ${o}"
            return accepted
        }
        return false;
    }

    private void testNotUsed() {
        Class clazz = ArrayList
        JrrJavassistUtils.findMethod(clazz, null, "", 1);

        Class class1 = com.intellij.idea.IdeaLogger.class;
        CtBehavior invokeMethod = JrrJavassistUtils.findMethod(class1, null, "logErrorHeader", 0);
    }

    public static JavassistCompletionBean isOkPsiElement(LeafPsiElement leafPsiElement) {
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            log.debug "not a string"
            return null;
        }
        JavassistCompletionBean completionBean = new JavassistCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.parent;
        if (!(parent1 instanceof PsiExpressionList)) {
            log.debug "not gr args"
            return null;
        }

//        PsiExpressionList args = (PsiExpressionList) parent1;
        completionBean.args = parent1
//        if(args.allArguments.length!=4){
//            log.debug "too small args"
//            return null
//        }
        PsiElement parent2 = parent1.parent
        if ((parent2 instanceof PsiMethodCallExpression)) {

        } else if (!(parent2 instanceof GrMethodCall)) {
            log.debug "not method"
            return null;
        }
        //GrMethodCall methodCall = (GrMethodCall) parent2;
        PsiElement child = parent2.children[0];
        if ((child instanceof PsiReferenceExpression)) {

        } else if (!(child instanceof GrReferenceExpression)) {
            log.debug "not gr ref"
            return null;
        }

        if (!(child.text?.contains('JrrJavassistUtils.'))) {
            log.debug "no jrrJavassistUtil method"
            return null;
        }

//        if (args.allArguments.length < 3) {
//
//            return null;
//        }
//        if (args.allArguments[1] != literalElemtnt) {
//            return null;
//        }

        PsiClass findPsiClass1 = findPsiClass(parent1)
        if (findPsiClass1 == null) {
            return null
        }
        completionBean.onObjectClass = findPsiClass1
        return completionBean;

    }

    private static PsiClass findPsiClass2(PsiClassType type2) {
        PsiClassType typeee = (PsiClassType) type2;
        PsiClass resolve = typeee.resolve()
        if (resolve != 'java.lang.Class') {
            return resolve
        }
        if (typeee.parameters == null || typeee.parameters.length != 1) {
            log.debug "bad generic ${typeee}"
            return null
        }
        if (typeee.parameters[0] instanceof PsiClassType) {
            PsiClassType sss = (PsiClassType) typeee.parameters[0]
            return sss.resolve()
        }
        log.debug "bad generic ${typeee.parameters[0]}"
        return null
    }

    private static PsiClass findPsiClass(PsiExpressionList parent1) {
        PsiType[] expressionTypes = parent1.getExpressionTypes()
        if ( expressionTypes == null || expressionTypes.length == 0) {
//log.debug "wrong args ${args.expressionTypes}"

        } else {
            PsiType type2 = expressionTypes[0]
            if (type2 instanceof PsiClassType) {
                return findPsiClass2(type2)
            }
        }
        if (!(parent1 instanceof GrArgumentList)) {
            log.debug "not gr args"
            return null;
        }
        GroovyPsiElement element1 = parent1.getAllArguments()[0]
        if (!(element1 instanceof GrReferenceExpression)) {
            log.debug "not ger ref"
            return null;
        }
        PsiElement varRef = element1.getSameNameVariants()[0].getElement();
        if (!(varRef instanceof GrVariable)) {
            log.debug "not a GrVar : ${varRef.class} ${varRef}"
            return null;
        }
//        GrVariable grVariable = varRef as GrVariable
        GrExpression initializerGroovy = varRef.getInitializerGroovy()
        if (initializerGroovy == null) {
            log.debug "init is null"
            return null
        }
        if (!(initializerGroovy instanceof GrReferenceExpression)) {
            log.debug "init not gr"
            return null
        }
        GroovyResolveResult[] variants = initializerGroovy.getSameNameVariants()
        if (variants.length != 1) {
            log.debug "no same vars"
            PsiType type = initializerGroovy.getType();
            if (!(type instanceof PsiClassType)) {
                return null
            }
            PsiClassType psiClassType = (PsiClassType) type;
            if (psiClassType.parameters.length != 1) {
                return null;
            }
            PsiType type1 = psiClassType.parameters[0]
            if (!(type1 instanceof PsiClassType)) {
                return null
            }
            PsiClassType psiClassType1 = (PsiClassType) type1;
            return psiClassType1.resolve();

        } else {
            PsiElement element = variants[0].element;
            if (!(element instanceof PsiClass)) {
                log.debug "not psi class"
                return null;
            }
            return element;
        }
    }


    @Override
    public boolean accepts(@Nullable Object o, ProcessingContext context) {
        return accepts(o);
    }

    @Override
    public ElementPatternCondition<PsiElement> getCondition() {
        log.debug(1)
        return new ElementPatternCondition(null);
    }

}
