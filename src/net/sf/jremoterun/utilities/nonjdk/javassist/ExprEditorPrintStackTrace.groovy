package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.CannotCompileException
import javassist.expr.ExprEditor
import javassist.expr.Handler;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

/**
 * Usage :
 * CtMethod.instrument(new ExprEditorPrintStackTrace())
 */
@CompileStatic
class ExprEditorPrintStackTrace extends ExprEditor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void edit(Handler h) throws CannotCompileException {
        h.insertBefore '$1.printStackTrace();'
        super.edit(h)
    }
}
