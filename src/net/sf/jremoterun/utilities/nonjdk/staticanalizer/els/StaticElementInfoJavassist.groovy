package net.sf.jremoterun.utilities.nonjdk.staticanalizer.els

import javassist.CtClass
import javassist.bytecode.ClassFile
import javassist.bytecode.FieldInfo;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class StaticElementInfoJavassist extends StaticElementInfo{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldInfo fieldInfo

    public ClassFile classFile;

    int lineNumber;
    CtClass ctClass

    @Override
    String getFieldName() {
        return fieldInfo.name
    }

    @Override
    String getClassName() {
        return classFile.name
    }

    @Override
    boolean isStatic() {
        return Modifier.isStatic(fieldInfo.accessFlags)
    }

    void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber
    }

    @Override
    int getLineNumber() {
        return lineNumber
    }

    @Override
    String getFileName() {
        return ctClass.simpleName
    }

    @Override
    String getPrintablePath() {
        return classFile.name
    }
}
