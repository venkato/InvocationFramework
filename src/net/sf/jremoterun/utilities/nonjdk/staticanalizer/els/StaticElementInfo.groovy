package net.sf.jremoterun.utilities.nonjdk.staticanalizer.els;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class StaticElementInfo {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // String className;
    // String fieldName;
//    public boolean isStatic;
     StaticFieldType fieldType;
    boolean isParentFile = false

    abstract String getFileName();
    abstract int getLineNumber();
    abstract String getFieldName();
    abstract String getClassName();
    abstract String getPrintablePath();
    abstract boolean isStatic();

    @Override
    String toString() {
        return "${getClassName()} ${getFileName()} ${printablePath} :${lineNumber}"
    }



}
