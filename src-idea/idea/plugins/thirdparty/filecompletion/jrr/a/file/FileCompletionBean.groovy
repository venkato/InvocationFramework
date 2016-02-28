package idea.plugins.thirdparty.filecompletion.jrr.a.file

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteral
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class FileCompletionBean {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);
    PsiLiteral literalElemtnt
    String value
    File parentFilePath
    PsiElement wholeFileDeclaration


    String getValueInLiteral(){
        Object value = literalElemtnt.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        // ;
        //int offset = value3.indexOf(addedConstant);
        String realValue = value.replace(IdeaMagic.addedConstant, '');
        return realValue
    }
}
