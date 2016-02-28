package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import javassist.CannotCompileException
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ExprEditorFindField extends ExprEditor {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String fieldName
    FieldAccess fieldAccess

    ExprEditorFindField(String fieldName) {
        this.fieldName = fieldName
    }

    @Override
    void edit(FieldAccess f) throws CannotCompileException {
//        log.info "${f.fieldName} ${f.lineNumber}"
        if(f.fieldName == fieldName) {
            boolean inpect2 = fieldAccess == null
            if (!inpect2) {
                inpect2 = fieldAccess != null && fieldAccess.lineNumber == -1
            }
            if (inpect2) {
                fieldAccess = f;
            }
        }
    }
}
