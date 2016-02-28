package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrSafeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrTypeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import org.jetbrains.plugins.groovy.lang.psi.impl.GrClassReferenceType

import java.lang.management.ManagementFactory

@CompileStatic
public class MyAcceptJrrProviderImpl implements ElementPattern<PsiElement> {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    @Override
    public boolean accepts(@Nullable Object o) {
        if (o instanceof com.intellij.psi.impl.source.tree.LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            boolean accepted = isOkPsiElement((LeafPsiElement) o) != null;
            log.debug "${o} ${accepted}"
            return accepted

        }
        return false;
    }

    private void testNotUsed() {
        Socket testVar = null;
        JrrClassUtils.getFieldValue(testVar, "")
        JrrClassUtils.getFieldValue(testVar.getChannel().keyFor(null), "attachment")
        JrrClassUtils.getFieldValue(ManagementFactory.properties as HashMap, "size")
        JrrClassUtils.getFieldValue(Class, "useCaches")
        JrrClassUtils.getFieldValue(Class, "vsf cachessdf")
        JrrClassUtils.getFieldValue(testVar, "bound")
        //JrrClassUtils.findMethod(testVar, "connect",2)
        JrrClassUtils.invokeJavaMethod(testVar, "close")
        JrrClassUtils.invokeJavaMethod(testVar, "connect")
    }

    public static JrrCompletionBean isOkPsiElement(LeafPsiElement leafPsiElement) {
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            log.debug "not psi literal ${parent}"
            return null;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            log.debug "not string ${value}"
            return null;
        }
        JrrCompletionBean completionBean = new JrrCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.parent;
        if (!(parent1 instanceof GrArgumentList)) {
            log.debug "not gr arg ${parent1}"
            return null;
        }

        GrArgumentList args = (GrArgumentList) parent1;
        completionBean.args = args
        if (args.allArguments.length < 2) {
            log.debug "bad arg count ${args.allArguments}"
            return null;
        }
        if (args.allArguments[1] != literalElemtnt) {
            log.debug "bad second arg ${args.allArguments[1]}"
            return null;
        }
        final GroovyPsiElement firstEl = args.allArguments[0]
        if ((firstEl instanceof GrMethodCallExpression)) {
            GrMethodCallExpression callExpression = (GrMethodCallExpression) firstEl
            PsiType psiType1 = callExpression.type
            if (!(psiType1 instanceof PsiClassType)) {
                log.debug "bad"
                return null
            }
            PsiClass resolve = psiType1.resolve()
            if (resolve.qualifiedName == 'java.lang.Class') {
                completionBean.onObjectStatic = true;
                GrExpression invokedExpression = callExpression.invokedExpression
                if (!(invokedExpression instanceof GrReferenceExpression)) {
                    log.debug "bad"
                    return null
                }
                GrExpression qualifier = (GrExpression) invokedExpression.getQualifier()
                PsiType psiType = qualifier.type
                if (psiType instanceof PsiClassType) {
                    completionBean.onObjectClass = psiType.resolve()
                } else {
                    log.debug "bad"
                    return null
                }

            } else {
                completionBean.onObjectClass = resolve
                completionBean.onObjectStatic = false;
            }
        } else if ((firstEl instanceof GrReferenceExpression)) {
            GrReferenceExpression onObject = (GrReferenceExpression) firstEl;
            PsiType firstArgumentType = onObject.type
            if (firstArgumentType instanceof PsiImmediateClassType) {
                if (onObject.sameNameVariants?.length != 1) {
                    log.debug "bad PsiImmediateClassType"
                    return null
                }

                PsiElement psiElement33 = onObject.sameNameVariants[0].element
                if (psiElement33 instanceof PsiClass) {
                    completionBean.onObjectClass = (PsiClass) psiElement33;
                    completionBean.onObjectStatic = true;
                } else if (psiElement33 instanceof PsiMethod) {
                    PsiMethod psiMethod = (PsiMethod) psiElement33
                    if (!(psiMethod.getName() == 'getClass')) {
                        log.debug "bad not class  2 ${psiElement33}"
                        return null
                    }
                    PsiElement element99 = onObject.children[0]
                    if (!(element99 instanceof GrReferenceExpression)) {
                        log.debug "bad not class "
                        return null
                    }
                    PsiType type = element99.getType()
                    if (!(type instanceof GrClassReferenceType)) {
                        log.debug "bad not class "
                        return null
                    }
                    completionBean.onObjectClass = type.resolve();
                    completionBean.onObjectStatic = true;
                } else {
                    log.debug "bad not class ${psiElement33}"
                    return null
                }

            } else if (firstArgumentType instanceof GrClassReferenceType) {
                GrClassReferenceType referenceType = (GrClassReferenceType) firstArgumentType;
                completionBean.onObjectClass = referenceType.resolve();
                completionBean.onObjectStatic = false;

            } else if (firstArgumentType instanceof PsiClassReferenceType) {
                PsiClassReferenceType referenceType = (PsiClassReferenceType) firstArgumentType;
                completionBean.onObjectClass = referenceType.resolve();
                completionBean.onObjectStatic = false;
            } else {
                log.debug "bad type ${firstArgumentType}"
                return null

            }
            //12312
        } else if (firstEl instanceof GrTypeCastExpression) {
            GrTypeCastExpression referenceType = (GrTypeCastExpression) firstEl;
            if (!(referenceType.type instanceof PsiClassType)) {
                log.debug "bad"
            }
            PsiClassType classType = (PsiClassType) referenceType.type
            completionBean.onObjectClass = classType.resolve();
            completionBean.onObjectStatic = false;
        } else if (firstEl instanceof GrSafeCastExpression) {
            GrSafeCastExpression referenceType = (GrSafeCastExpression) firstEl;
            if (!(referenceType.type instanceof PsiClassType)) {
                log.debug "bad"
            }
            PsiClassType classType = (PsiClassType) referenceType.type
            completionBean.onObjectClass = classType.resolve();
            completionBean.onObjectStatic = false;

        } else {
            log.debug "bad first arg ${firstEl}"
            return null;
        }

        if (completionBean.onObjectClass == null) {
            log.debug "on object class is null"
            return null
        }
        PsiElement parent2 = args.parent
        if (!(parent2 instanceof GrMethodCall)) {
            return null;
        }
        GrMethodCall methodCall = (GrMethodCall) parent2;
        PsiElement child = methodCall.children[0];
        if (!(child instanceof GrReferenceExpression)) {
            return null;
        }

        GrReferenceExpression referenceExpression = (GrReferenceExpression) child;
        if (!(referenceExpression.text?.contains('JrrClassUtils.'))) {
            return null;
        }

        completionBean.methodName = ReflectionElement.valueOf(referenceExpression.referenceName);

        return completionBean;

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
