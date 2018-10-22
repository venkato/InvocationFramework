package idea.plugins.thirdparty.filecompletion.jrr.a.maven

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrSafeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.impl.GrClassReferenceType
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.typedef.enumConstant.GrEnumConstantImpl

@CompileStatic
public class MyAcceptMavenProviderImpl implements ElementPattern<PsiElement> {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    @Override
    public boolean accepts(@Nullable Object o) {
        //log.debug "timezone ${o}"
        if (o instanceof com.intellij.psi.impl.source.tree.LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            boolean element = isOkPsiElement(o);
            log.debug "cp3 ${element}"
            return element
        }
        return false;
    }

    public static boolean isOkPsiElement(LeafPsiElement leafPsiElement) {
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            return false;
        }
        JavassistCompletionBean completionBean = new JavassistCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.parent;
        if (parent1 instanceof GrSafeCastExpression) {
            String text = parent1.getType().getPresentableText()
            if (text != null && text.contains(MavenId.simpleName)) {
                return true
            }
            return false
        }
        if (!(parent1 instanceof GrArgumentList)) {
            return false;
        }

        completionBean.args = parent1
        if (parent1.getAllArguments().length != 1) {
            return false;
        }
        PsiElement parent2 = parent1.getParent()
        if (parent2 instanceof GrEnumConstantImpl) {
            boolean enumCheck = checkEnum(parent2)
//            log.info "enumCheck = ${enumCheck}"
            return enumCheck
        }
        if (!(parent2 instanceof GrNewExpression)) {
            return false;
        }
        PsiType type = parent2.getType();
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return false;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || !(psiClass.name.contains(MavenId.getSimpleName()))) {
            log.debug "not a maven id"
            return false;
        }

        return true;

    }

    static boolean checkEnum(GrEnumConstantImpl e) {
        PsiType type = e.getType()
        if (type == null) {
            log.info "enum type is null for ${e}"
            return false
        }
        PsiType[] types = type.getSuperTypes()
        if (types == null) {
            log.info("super type is null for ${type}")
            return false
        }
        List<GrClassReferenceType> grClassReferenceTypes = (List) types.toList().findAll {
            it instanceof GrClassReferenceType
        }
        if (grClassReferenceTypes.size()==0) {
            log.info("GrClassReferenceType not found for ${type}")
            return false
        }
        GrClassReferenceType type1 = grClassReferenceTypes.find { it.getName() == MavenIdContains.getSimpleName() }
        boolean found = type1 != null
        if(!found){
            type1 = grClassReferenceTypes.find { it.getName() == MavenIdAndRepoContains.getSimpleName() }
            found = type1 != null
        }

        if (found) {
            log.info "type1 = ${type1}"
        } else {
            log.info "Maven type not found from : ${grClassReferenceTypes}"
        }
        return found

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


    private void testNotUsed() {
        new MavenId('cglib:cglib-nodep:3.2.0');
        MavenId m = 'cglib:cglib-nodep:3.2.0' as MavenId
    }


    enum notUsedEnum implements MavenIdContains {
        aa('cglib:cglib-nodep:3.2.0')
        , bb("cglib:cglib-nodep:3.2.0")
        ;

        MavenId m;

        notUsedEnum(String m) {
            this.m = new MavenId(m)
        }
    }


}
