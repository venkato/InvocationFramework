package idea.plugins.thirdparty.filecompletion.jrr.a.file

import com.intellij.lang.jvm.JvmMember
import com.intellij.lang.jvm.JvmTypeDeclaration
import com.intellij.lang.jvm.types.JvmReferenceType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiCompiledElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.PsiType
import com.intellij.psi.PsiVariable
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectly
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectlyMoreComplex
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrSafeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrAccessorMethod
import org.jetbrains.plugins.groovy.lang.psi.impl.GrClassReferenceType
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrNewExpressionImpl
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrReferenceExpressionImpl

@CompileStatic
public class MyAcceptFileProviderImpl implements ElementPattern<PsiElement> {

    // this is log file
    public static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    @Override
    boolean accepts(@Nullable Object o) {
        log.debug " accept : ${o?.class.name}"
        if (o instanceof LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            boolean accept = isOkJavaAndGroovyPsiElement(o) != null;
            return accept;
        }
        return false;
        //
    }

    static File isOkJavaPsiElement3(PsiJavaToken leafPsiElement) {
        PsiElement parent = leafPsiElement.parent.parent.parent
        if (!(parent instanceof PsiNewExpression)) {
            log.debug("not new expression")
            return null;
        }
//        PsiNewExpression newExpression = (PsiNewExpression) parent
        log.debug "${parent}"
        return javaFileViaNewExpression7(parent, true);
    }

    static FileCompletionBean isOkJavaPsiElement(PsiJavaToken leafPsiElement, boolean addSuffix) {
        PsiElement parent = leafPsiElement.parent.parent.parent
        if (!(parent instanceof PsiNewExpression)) {
            log.debug("not new expression")
            return null;
        }
//        PsiNewExpression newExpression = (PsiNewExpression) parent
        log.debug "${parent}"
        PsiNewExpression pn = parent
        TwoResult<Boolean, File> res3 = javaFileViaNewExpression3(pn, addSuffix);
        if (res3 == null) {
            return null
        }
        PsiExpression[] argsExpressions = pn.argumentList.expressions
        PsiExpression element = argsExpressions.toList().last()
        String value2 = getStringFromPsiLiteral(element);
        if (value2 == null) {
            log.debug "not a string"
            return null;
        }
        FileCompletionBean fileCompletionBean = new FileCompletionBean()
        fileCompletionBean.wholeFileDeclaration = parent
        fileCompletionBean.value = value2
        fileCompletionBean.parentFilePath = res3.second
        return fileCompletionBean

    }

    static FileCompletionBean isOkJavaAndGroovyPsiElement(LeafPsiElement leafPsiElement) {
        if (leafPsiElement instanceof PsiJavaTokenImpl) {
            return isOkJavaPsiElement(leafPsiElement, false)
        }
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            return null;
        }
        Object value = parent.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        FileCompletionBean completionBean = new FileCompletionBean();
        completionBean.value = getStringFromPsiLiteral(parent)
        if (completionBean.value == null) {
            return null;
        }
        // log.debug "value = ${completionBean.value}"
        completionBean.literalElemtnt = parent;
        PsiElement parent1 = parent.parent;
        PsiElement parent2 = parent1.parent
        if (parent1 instanceof GrArgumentList) {
            if (parent2 instanceof GrNewExpression) {
                TwoResult<Boolean, File> res = fileViaGrNewExpression(parent2)
                if (res != null && res.first) {
                    completionBean.wholeFileDeclaration = parent2
                    completionBean.parentFilePath = res.second
                    return completionBean
                }
                return null
            }
        }
        if (parent1 instanceof GrSafeCastExpression) {
            File file = resolveFileFromSafeCast(parent1)
            if (file != null) {
                completionBean.wholeFileDeclaration = parent1
                return completionBean
            }

        }

        if (parent2 instanceof GrMethodCallExpression) {
            File file = fileViaFileChildMethod(parent2, false)
            if (file != null) {
                completionBean.wholeFileDeclaration = parent1
                completionBean.parentFilePath = file
                return completionBean
            }

        }

        return null;
    }

    /**
     * Calculate path for var.
     * Example : File parent = new File('/opt');
     * use parent somewhere. This method calc path for parent var
     */
    static File findFileFromVarGeneric(final PsiElement varRef) {
        if (varRef == null) {
            log.warn("ref is null", new Exception())
            return null
        }
        if (varRef instanceof GrReferenceExpression) {
            // may be use resolve ?
            // varRef.resolve()
            File result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFileViaGrRef(varRef)
            if(result!=null){
                return result;
            }
            GroovyResolveResult[] variants = varRef.getSameNameVariants()
            if (variants == null) {
                log.debug "variants is null"
                return null
            }
            if (variants.length != 1) {
                log.debug "args not 1"
                return null
            }
            PsiElement element = variants[0].element
            if (element == null) {
                log.debug "failed resolve el var for ${varRef}"
                return null
            }
            if (element == varRef) {
                log.info "cycle ${varRef}"
                return null
            }
            return findFileFromVarGeneric(element)
        }
        if (varRef instanceof PsiReferenceExpression) {
            File result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFileViaJavaRef(varRef)
            if(result!=null){
                return result;
            }
            PsiElement resolve = varRef.resolve()
            if (resolve == null) {
                log.info "failed resolve ${varRef}"
                return null
            }
            if (resolve == varRef) {
                log.info "cycle : ${varRef}"
                return null
            }
            return findFileFromVarGeneric(resolve)
        }
        if (varRef instanceof GrField) {
            File result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFile(varRef)
            if(result!=null){
                return result
            }
            GrExpression gr1 = varRef.getInitializerGroovy()
            if (gr1 == null) {
                log.debug "null init for ${varRef}"
                return null
            }
            return findFileFromVarGeneric(gr1)
        }
        if (varRef instanceof PsiField) {
            PsiClass containingClass = varRef.getContainingClass()
            String name = varRef.getName()
            String cassName = containingClass.getQualifiedName()
            boolean canResolve = FieldResolvedDirectly.fieldResolvedDirectly.canResolve(cassName,name)
            if(canResolve){
                return FieldResolvedDirectly.fieldResolvedDirectly.resolveValue(cassName,name)
            }
            return javaFindFileFromField(varRef)
        }
        if (varRef instanceof GrVariable) {
            GrExpression gr1 = varRef.getInitializerGroovy()
            if (gr1 == null) {
                log.debug "null init for ${varRef}"
                return null
            }
            return findFileFromVarGeneric(gr1)
        }
        if (varRef instanceof GrAccessorMethod) {
            PsiElement navigationElement = varRef.getNavigationElement()
            if (navigationElement == null) {
                log.info "navigation el is null"
            } else {
                if (navigationElement != varRef) {
                    return findFileFromVarGeneric(navigationElement)
                }
            }
        }

        // new begin
        if (varRef instanceof GrSafeCastExpression) {
            return resolveFileFromSafeCast(varRef)
        }
        if (varRef instanceof GrNewExpression) {
            return fileViaGroovyNewExpression3(varRef, true);
        }
        if (varRef instanceof GrMethodCallExpression) {
            return fileViaFileChildMethod(varRef, true);
        }
        if (varRef instanceof PsiNewExpression) {
            javaFileViaNewExpression7(varRef, true)
        }
        // new end

        log.debug "not a GrVar : ${varRef.class} ${varRef}"
        return null;
    }

    static String getStringFromPsiLiteral(PsiElement psiElement) {
        if (!(psiElement instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral literalElemtnt = (PsiLiteral) psiElement;
        Object value = literalElemtnt.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        return value.replace(IdeaMagic.addedConstant, '');
    }


    private static File resolveFileFromSafeCast(GrSafeCastExpression castExpression) {
        String text = castExpression.type.presentableText
        if (text != null && text.contains('File')) {
            log.debug("accpted")
            String literal = getStringFromPsiLiteral(castExpression.operand)
            if (literal == null) {
                log.debug "not a tsring"
                return null;
            }
            return new File(literal);
        }
        return null
    }

    /**
     * Internal method
     */
    private
    static TwoResult<Boolean, File> fileViaGrNewExpression(GrNewExpression grExpression) {
        PsiType type = grExpression.getType();
        if (!(type instanceof GrClassReferenceType)) {
            return null
        }
        PsiClass resolve = type.resolve()

        if (resolve == null || !(resolve.getName().contains('File'))) {
            log.debug("accpted")
            return null
        }
        if (grExpression.argumentList.allArguments.length == 1) {
            String literal = getStringFromPsiLiteral(grExpression.argumentList.allArguments[0]);
            if (literal == null) {
                log.debug "not a string"
                return null
            }
            return new TwoResult<Boolean, File>(true, null);
        }
        if (grExpression.argumentList.allArguments.length == 2) {
            File parentFilePath = fileViaGroovyNewExpression3(grExpression, false)
            if (parentFilePath == null) {
                return null
            }
            return new TwoResult<Boolean, File>(true, parentFilePath);
//            return completionBean.parentFilePath != null;
        }
        return null
    }

    /**
     * Resolve java construction new File('path')
     * java construction not supported : new File(parent,'child')
     * but for groovy supported
     * @param grExpression
     * @param addSuffix
     * @return
     */
    private
    static TwoResult<Boolean, File> javaFileViaNewExpression3(PsiNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        PsiExpression[] argsExpressions = grExpression.argumentList.expressions
        if (argsExpressions.length == 1) {
            return new TwoResult<Boolean, File>(true, null);
        }
        if (argsExpressions.length == 2) {
            log.debug "too many args"
            File fff = javaFileViaNewExpression7(grExpression, addSuffix)
            if (fff == null) {
                return null;
            }
            return new TwoResult<Boolean, File>(true, fff);


        }
        log.debug "not implemented"
        return null
    }

    private static File javaFileViaNewExpression7(PsiNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        PsiExpression[] argsExpressions = grExpression.argumentList.expressions
        if (argsExpressions.length == 1) {
            PsiExpression childEl = argsExpressions[0]
            if (!(childEl instanceof PsiLiteralExpression)) {
                log.info "not psi literal : ${childEl}"
                return null
            }
            String value2 = getStringFromPsiLiteral(childEl);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new File(value2)
        }
        if (argsExpressions.length != 2) {
            log.debug "too many args"
            return null;
        }
        PsiExpression childEl = argsExpressions[1]
        if (!(childEl instanceof PsiLiteralExpression)) {
            log.info "not psi literal : ${childEl}"
            return null
        }
        File parentFile = findFileFromVarGeneric(argsExpressions[0])
        if (parentFile == null) {
            return null
        }
        if (!addSuffix) {
            return parentFile
        }
        String value2 = getStringFromPsiLiteral(childEl);
        if (value2 == null) {
            log.debug "not a string"
            return null;
        }
        return new File(parentFile, value2)
    }

    private static File javaFileViaNewExpression2(PsiNewExpression grExpression) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        if (grExpression.argumentList.expressions.length == 1) {
            PsiExpression element = grExpression.argumentList.expressions[0]
            String value2 = getStringFromPsiLiteral(element);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new File(value2);
        }
        if (grExpression.argumentList.expressions.length != 2) {
            log.debug "too many args"
            return null;
        }
        log.debug "not implemented"
        return null
    }

    /**
     * Resolve groovy construction new File('path') and new File(parent,'path')
     * @param grExpression
     * @param addSuffix indecatios if need resolve child path for 2 args constructor
     * @return
     */
    private static File fileViaGroovyNewExpression3(GrNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof GrClassReferenceType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass resolve = type.resolve()

        if (resolve == null || !(resolve.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        if (grExpression.argumentList.allArguments.length == 1) {
            GroovyPsiElement element = grExpression.argumentList.allArguments[0]
            String value2 = getStringFromPsiLiteral(element);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new File(value2);
        }
        if (grExpression.argumentList.allArguments.length != 2) {
            log.debug "too many args"
            return null;
        }
        GroovyPsiElement arg = grExpression.argumentList.allArguments[0]
        File fileParent = findFileFromVarGeneric(arg);
        if (fileParent == null) {
            return null
        }
        if (!addSuffix) {
            return fileParent
        }
        String suffixFile = getStringFromPsiLiteral(grExpression.argumentList.allArguments[1]);
        if (suffixFile == null) {
            return null
        }
        return new File(fileParent, suffixFile)
    }

    /**
     * Calculate path for field.
     * Example : File parent = new File('/opt');
     * use parent somewhere. This method calc path for parent field
     */
    private static File javaFindFileFromField(final PsiField psiField) {
        PsiField psiField2 = psiField
        if (psiField2.navigationElement != null && psiField2.navigationElement instanceof PsiField) {
            log.debug "use navigation el"
            psiField2 = psiField2.navigationElement as PsiField
        }
        PsiExpression initializer = psiField2.initializer
        if (initializer instanceof PsiNewExpression) {
            File fileResolved = javaFileViaNewExpression2(initializer);
            return fileResolved;
        }
        if (initializer == null) {
            if (!(psiField2 instanceof PsiCompiledElement)) {
                log.debug "not a psi compile"
                return null;
            }
            PsiElement mirror = psiField2.getMirror();
            if (!(mirror instanceof PsiField)) {
                log.debug "not a field"
                return null;
            }
            psiField2 = mirror;
        } else {
            log.info "not imeplementted initializer : ${initializer.class.name} ${initializer}"
            return null
        }
        if (psiField2 instanceof GrField) {
            return findFileFromVarGeneric(psiField2.getInitializerGroovy())
        }
        if (psiField2 != psiField) {
            return javaFindFileFromField(psiField2)
        }
        log.debug "not gr field ${psiField2?.class.name} ${psiField2}"

        return null
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


    private static File resolveToFileMethod(GrMethodCallExpression grExpression) {
        // invokedExpression = GitReferences.purejavacommTraffSrc.resolveToFile()
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.resolveToFile.name()
        // firstChild  = GitReferences.purejavacommTraffSrc;
        PsiElement firstChild = invokedExpression.getFirstChild()
        if (!(firstChild instanceof GrReferenceExpression)) {
            log.info "not GrRef"
            return null
        }
        // child2 = GitReferences
        PsiElement child2 = firstChild.getFirstChild()
        if (!(child2 instanceof GrReferenceExpression)) {
            log.info "firstChild no psi : ${child2}"
            return null
        }
        PsiElement psiField = firstChild.resolve()
        if (!(psiField instanceof PsiField)) {
            log.info("not psi field : ${psiField}")
            return  null
        }
        PsiField psiVar = psiField as PsiField
        PsiClass containingClass = psiVar.getContainingClass()
//        PsiType type1 = psiVar.getType()
//        if (!(type1 instanceof PsiClass)) {
//            log.info("not psi class : ${type1}")
//            return null
//        }
//        PsiClass psiClass = type1 as PsiClass
        String className1 = containingClass.getQualifiedName();
        PsiIdentifier pi = psiVar.getNameIdentifier();
        String fieldName =     pi.getText();
        boolean canResolveB=FieldResolvedDirectly.fieldResolvedDirectly.canResolveEnum(className1);
        if(!canResolveB){
            log.info "not in scope : ${className1}"
            return null
        }

        Object fieldValue = FieldResolvedDirectly.fieldResolvedDirectly.resolveValue2(className1, fieldName);
//        if (!(fieldValue instanceof GitSpec)) {
//            log.info "not git spec : ${fieldValue}"
//            return null
//        }
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        File f =  handler.resolveToFileIfDownloaded(fieldValue)
        log.info "resolved : ${f}"
        return f
    }

    private static File childLazyMethodResolveParent(PsiElement firstChild ) {
        if (firstChild instanceof GrNewExpressionImpl) {
            PsiType type = firstChild.getType();
            if (!(type instanceof GrClassReferenceType)) {
                return null
            }
            PsiClass resolve = type.resolve()

            if (resolve == null) {
                log.info "not  git spec 1 ${type}"
                return null
            }
            boolean isSvnSpec= false;
            if (!(resolve.getName().contains(GitSpec.getSimpleName()))) {
                log.info("not  git spec: ${resolve}")
                if(resolve.getName().contains(SvnSpec.getSimpleName())){
                    isSvnSpec = true
                }else {
                    return null
                }
            }
            GrArgumentList args = firstChild.getArgumentList()
            GroovyPsiElement[] arguments = args.getAllArguments()
            if (arguments == null || arguments.length != 1) {
                log.info "bad args : ${arguments}"
                return null
            }
            GroovyPsiElement firstArg = arguments[0]
            if (firstArg instanceof GrLiteral) {
                GrLiteral literal = (GrLiteral) firstArg;
                String  gitRefValue = literal.getValue()
                CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                if(handler==null){
                    throw new IllegalStateException("customObjectHandler was not set")
                }
                Object gitSpec;
                if(isSvnSpec){
                    gitSpec = new SvnSpec(gitRefValue)
                }else{
                    gitSpec = new GitSpec(gitRefValue)
                }
                File f =  handler.resolveToFileIfDownloaded(gitSpec)
                if(f==null){
                    log.info "failed resolved ${gitSpec}"
                }
                return f;
            }
        }
        if (!(firstChild instanceof GrReferenceExpression)) {
            log.info "not GrRef ${firstChild.getClass()} ${firstChild}"
            return null
        }
        // child2 = GitReferences
        PsiElement child2 = firstChild.getFirstChild()
        if (!(child2 instanceof GrReferenceExpression)) {
            log.info "firstChild no psi : ${child2}"
            return null
        }
        PsiElement psiField = firstChild.resolve()
        if (!(psiField instanceof PsiField)) {
            log.info("not psi field : ${psiField}")
            return null
        }
        PsiField psiVar = psiField as PsiField
        PsiClass containingClass = psiVar.getContainingClass()
//        PsiType type1 = psiVar.getType()
//        if (!(type1 instanceof PsiClass)) {
//            log.info("not psi class : ${type1}")
//            return null
//        }
//        PsiClass psiClass = type1 as PsiClass
        String className1 = containingClass.getQualifiedName();
        PsiIdentifier pi = psiVar.getNameIdentifier();
        String fieldName = pi.getText()
        boolean canResolveB = FieldResolvedDirectly.fieldResolvedDirectly.canResolveEnum(className1)
        if (!canResolveB) {
            log.info "not in scope : ${className1}"
            return null
        }

        Object fieldValue = FieldResolvedDirectly.fieldResolvedDirectly.resolveValue2(className1, fieldName);
//        if (!(fieldValue instanceof GitSpec)) {
//            log.info "not git spec : ${fieldValue}"
//            return null
//        }
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if (handler == null) {
            throw new IllegalStateException("customObjectHandler was not set")
        }
        File f = handler.resolveToFileIfDownloaded(fieldValue)
        log.info "resolved : ${f}"
        return f;
    }

    private static File childLazyMethod(GrMethodCallExpression grExpression, boolean addSuffix) {
        // invokedExpression = GitReferences.purejavacommTraffSrc.resolveToFile()
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.childL.name()
        // firstChild  = GitReferences.purejavacommTraffSrc;
        PsiElement firstChild = invokedExpression.getFirstChild()
        File f =  childLazyMethodResolveParent(firstChild);
        if(f ==null){
            return null
        }
        if(!addSuffix) {
            return f
        }
        String suffixFile = getStringFromPsiLiteral(grExpression.argumentList.allArguments[0]);
        if (suffixFile == null) {
            return null
        }
        return new File(f, suffixFile)

    }

    private
    static File fileViaFileChildMethod(GrMethodCallExpression grExpression, boolean addSuffix) {

        GrExpression invokedExpression = grExpression.invokedExpression
        if (!(invokedExpression instanceof GrReferenceExpressionImpl)) {
            log.info "not GrReferenceExpressionImpl : ${invokedExpression.class.name} ${invokedExpression}"
            return null
        }
        String methodName = invokedExpression.getReferenceName()
        if(methodName == SpecialMethodName.resolveToFile.name()){
            return resolveToFileMethod(grExpression)
        }
        if (methodName == SpecialMethodName.childL.name()) {
            return childLazyMethod(grExpression,addSuffix)
        }
        if (methodName != SpecialMethodName.child.name()) {
            log.debug "method name is not child : ${methodName}"
            return null
        }
        PsiElement[] children = grExpression.children
        if (children.length != 2) {
            log.debug "too low childerns"
            return null
        }
        File parentFile = findFileFromVarGeneric(invokedExpression.getFirstChild())
        if (parentFile == null) {
//            log.debug "parent file not found"
            return null
        }
        if (!addSuffix) {
            return parentFile
        }
        String suffixFile = getStringFromPsiLiteral(grExpression.argumentList.allArguments[0]);
        if (suffixFile == null) {
            return null
        }
        return new File(parentFile, suffixFile)
    }

    private void notUsed() {
        File f = "c:/1/" as File

    }

}
