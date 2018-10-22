package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.lang.jvm.JvmTypeDeclaration
import com.intellij.lang.jvm.types.JvmReferenceType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiImmediateClassType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.codehaus.janino.ExpressionEvaluator
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

import java.util.logging.Logger

@CompileStatic
class FieldResolvedDirectlyMoreComplex {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static FieldResolvedDirectlyMoreComplex fieldResolvedDirectlyMoreComplex = new FieldResolvedDirectlyMoreComplex();

    File tryResolveFileViaJavaRef(PsiReferenceExpression grReferenceExpression) {
        PsiElement[] children = grReferenceExpression.getChildren()
        if (children == null) {
            log.info "no childs"
            return null
        }
        PsiElement firstChild = grReferenceExpression.getFirstChild()
        if (firstChild == null) {
            log.info "no first childs"
            return null
        }
        if (!(firstChild instanceof PsiReferenceExpression)) {
            log.info "firstChild no psi : ${firstChild}"
            return null
        }
        PsiType type = firstChild.getType();
        return commonEnum(type, grReferenceExpression);
    }

    File commonEnum(PsiType type, PsiElement psiElement) {
        log.info("resolving : ${psiElement.getText()}")
        if (!(type instanceof PsiClassType)) {
            log.info("type not PsiClassType: ${type}")
            return null;
        }
        PsiClass resolve = type.resolve();
        String qualifiedName = resolve.getQualifiedName();
        boolean canResolveB=FieldResolvedDirectly.fieldResolvedDirectly.canResolveEnum(qualifiedName)
        if (!canResolveB) {
            return null;
        }
        return resolveViaJavaExpression(qualifiedName,psiElement.getText());
    }

    File resolveViaJavaExpression(String className,String expression){
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setParameters(new String[0], new Class[0]);

        ee.setExpressionType(Object);
        List<String> impo = []
        impo.add(className)
        ee.setParentClassLoader(FieldResolvedDirectly.fieldResolvedDirectly.classLoader)
        ee.setDefaultImports(impo.toArray(new String[0]));
        ee.cook(expression)
        Object evaluate = ee.evaluate(new Object[0])
        if (evaluate instanceof File) {
            File f = (File) evaluate;
            log.info "resolved ${expression} to ${f}"
            return f;
        }
        throw new Exception("got not file : ${expression} : ${evaluate}")

    }


    File tryResolveFileViaGrRef(GrReferenceExpression grReferenceExpression) {
        PsiElement[] children = grReferenceExpression.getChildren()
        if (children == null) {
            log.info("no childs")
            return null
        }
        if (children.length != 1) {
            log.info("childs not one : ${children.length}")
            return null;
        }
        PsiElement psiElementChild = children[0]
        if (!(psiElementChild instanceof GrReferenceExpression)) {
            log.info("psiElementChild not grRef: ${psiElementChild}")
            return null
        }
        PsiType type = psiElementChild.getType();
        return commonEnum(type, grReferenceExpression);

    }

    File tryResolveFile(GrField varRef) {
        PsiClass containingClass = varRef.getContainingClass()
        String name = varRef.getName()
        String cassName = containingClass.getQualifiedName()
        boolean canResolve = FieldResolvedDirectly.fieldResolvedDirectly.canResolve(cassName, name)
        if (canResolve) {
            return FieldResolvedDirectly.fieldResolvedDirectly.resolveValue(cassName, name)
        }
        return null

    }
}
