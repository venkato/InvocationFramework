package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiLiteral
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList

@CompileStatic
class JrrCompletionBean {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    ReflectionElement methodName
    PsiClass onObjectClass
    boolean onObjectStatic
    PsiLiteral literalElement
    GrArgumentList args


    String getValueInLiteral(){
        Object value = literalElement.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        // ;
        //int offset = value3.indexOf(addedConstant);
        String realValue = value.replace(IdeaMagic.addedConstant, '');
        return realValue
    }
}
