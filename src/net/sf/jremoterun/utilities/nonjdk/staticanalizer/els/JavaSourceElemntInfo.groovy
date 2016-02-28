package net.sf.jremoterun.utilities.nonjdk.staticanalizer.els

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.ObjectCreationExpr
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode

import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaSourceElemntInfo extends StaticElementInfo{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public VariableDeclarator fieldNode;
    public FieldDeclaration fieldDeclaration
    public ObjectCreationExpr expression;
    public CompilationUnit classNode;
    public TypeDeclaration clOrInt
    public File printablePath2;



    @Override
    String getPrintablePath() {
        return printablePath2
    }

    @Override
    String getClassName() {
        return "${classNode.packageDeclaration.get().name}.${clOrInt.name}"
    }

    @Override
    String getFieldName() {
        return fieldNode.getName()
    }


    @Override
    boolean isStatic() {
//        return Modifier.isStatic(fieldNode.modifiers)
        if (clOrInt instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration  clOrInt= (ClassOrInterfaceDeclaration) clOrInt;
            if(clOrInt.isInterface()){
                return true
            }
        }
        if(fieldDeclaration==null){
            return false;
        }
        return fieldDeclaration.static
    }

    @Override
    int getLineNumber() {
        return expression.getBegin().get().line
    }

    @Override
    String getFileName() {
//        return classNode.nameWithoutPackage
        return clOrInt.name.asString();
    }

}
