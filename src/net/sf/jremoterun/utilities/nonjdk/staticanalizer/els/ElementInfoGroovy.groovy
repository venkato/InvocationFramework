package net.sf.jremoterun.utilities.nonjdk.staticanalizer.els;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode

import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ElementInfoGroovy extends StaticElementInfo{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldNode fieldNode;
    public ClassNode classNode;
    public String printablePath;

    void setPrintablePath(String printablePath) {
        this.printablePath = printablePath
    }

    @Override
    String getPrintablePath() {
        return printablePath
    }

    @Override
    String getClassName() {
        return classNode.getName()
    }

    @Override
    String getFieldName() {
        return fieldNode.getName()
    }


    @Override
    boolean isStatic() {
        return Modifier.isStatic(fieldNode.modifiers)
    }

    @Override
    int getLineNumber() {
        return fieldNode.getLineNumber()
    }

    @Override
    String getFileName() {
        return classNode.nameWithoutPackage
    }

}
