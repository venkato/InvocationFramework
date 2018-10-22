package net.sf.jremoterun.utilities.nonjdk.staticanalizer;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ClassNameReference
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.ElementMethodGroovy
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.SourceUnit;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ExpressionFinder extends ClassCodeVisitorSupport {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Set<Integer> ignoreLines;

    LoaderStuff loaderStuff

    ClassNode classNode;
    String elName;
    String printablePath;

    String methodName

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        methodName = node.name
        super.visitConstructorOrMethod(node, isConstructor)
        methodName = null
    }



    @Override
    void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression)
        visitCastExpression1(expression)
    }

    boolean visitCastExpression1(CastExpression expression) {
        if(!ignoreLines.contains(expression.lineNumber)) {
            String nameWithoutRef = expression.type.nameWithoutPackage
            boolean isFileExp = nameWithoutRef == 'File'
            if (isFileExp || nameWithoutRef == ClRef.simpleName) {
                Expression nestedExpression = expression.expression
                if (nestedExpression instanceof ConstantExpression) {
                    ConstantExpression ce = (ConstantExpression) nestedExpression;
                    castExpressionImpl2(expression, ce, isFileExp)
                    return true
                }
            }
        }
        return false
    }

    void castExpressionImpl2(Expression parentExpression, ConstantExpression ce, boolean isFileExp){
        String text = ce.text
        ElementMethodGroovy elementMethodGroovy = new ElementMethodGroovy();
        elementMethodGroovy.expression =parentExpression;
        elementMethodGroovy.fieldName = methodName
        elementMethodGroovy.className = elName
        elementMethodGroovy.printablePath = printablePath
        elementMethodGroovy.isParentFile = true
        elementMethodGroovy.fieldType = isFileExp ? StaticFieldType.file : StaticFieldType.cnr
        if(isFileExp){
            if(CurrentDirDetector. isCurrentDir(text)){

            }else {
                File f = text as File
                if (f.exists()) {
                    loaderStuff.onFileFound(elementMethodGroovy, f)
                } else {
                    loaderStuff.problemFound(elementMethodGroovy, text)
                }
            }
        }else{
            try {
                if(!loaderStuff.checkClassExists(text)){
                    loaderStuff.problemFound(elementMethodGroovy,text)
                }
//                loaderStuff.loadClass(text)
//            }catch (ClassNotFoundException e){
//                loaderStuff.problemFound(elementMethodGroovy,text)
            }catch(Throwable e){
                loaderStuff.onException(e,parentExpression)
            }
        }
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression expression) {
        visitConstructorCallExpression2(expression)
    }

    boolean visitConstructorCallExpression2(ConstructorCallExpression expression) {
        super.visitConstructorCallExpression(expression)
        if(!ignoreLines.contains(expression.lineNumber)) {
            String nameWithoutRef = expression.type.nameWithoutPackage
            boolean isFileExp = nameWithoutRef == 'File'
            if (isFileExp || nameWithoutRef == ClRef.simpleName) {
                Expression arguments = expression.arguments
                if (arguments instanceof ArgumentListExpression) {
                    ArgumentListExpression list = (ArgumentListExpression) arguments;
                    if (list.expressions.size() == 1) {
                        Expression nestedExpression = list.expressions[0]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;
                            castExpressionImpl2(expression, ce, isFileExp)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }
}
