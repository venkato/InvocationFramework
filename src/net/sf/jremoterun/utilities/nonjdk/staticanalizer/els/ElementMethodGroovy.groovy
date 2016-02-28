package net.sf.jremoterun.utilities.nonjdk.staticanalizer.els

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.Expression

import java.util.logging.Logger

@CompileStatic
class ElementMethodGroovy extends StaticElementInfo{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public Expression expression;
//    public ClassNode classNode;
//    String nameContext;
    String className;
    String fieldName
    String printablePath



    @Override
    String getPrintablePath() {
        return printablePath
    }

    @Override
    String getClassName() {
        return className
    }

    @Override
    String getFieldName() {
        return fieldName
    }


    @Override
    boolean isStatic() {
        return false
    }

    @Override
    int getLineNumber() {
        return expression.getLineNumber()
    }

    @Override
    String getFileName() {
        return className
    }

}
