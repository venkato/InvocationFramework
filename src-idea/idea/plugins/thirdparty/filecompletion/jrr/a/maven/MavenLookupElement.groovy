package idea.plugins.thirdparty.filecompletion.jrr.a.maven

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.StaticallyImportable
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.codeInsight.lookup.LookupElementPresentation
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import javax.swing.*

@CompileStatic
class MavenLookupElement extends LookupElementDecorator implements StaticallyImportable {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    String offset;
    Icon icon

    protected MavenLookupElement(LookupElement delegate, String offset, Icon icon) {
        super(delegate)
        this.offset = offset;
        this.icon = icon
    }



    @Override
    void setShouldBeImported(boolean shouldImportStatic) {
        log.debug "shouldImportStatic ${shouldImportStatic}"
    }

    @Override
    boolean canBeImported() {
        // log.debug "canBeImported"
        return true
    }

    @Override
    boolean willBeImported() {
        // log.debug "willBeImported"
        return true
    }



    @Override
    void renderElement(LookupElementPresentation presentation) {
        presentation.icon = icon
        presentation.setItemText(offset);
    }

    @Override
    void handleInsert(InsertionContext context) {
        log.debug "handle insert ${context}"
//        super.handleInsert(context)
    }

    enum notUsedEnum implements MavenIdContains {
        aa('cglib:cglib-nodep:3.2.0')
        ,bb("cglib:cglib-nodep:3.2.0")
        ;

        MavenId m;

        notUsedEnum(String m) {
            this.m = new MavenId(m)
        }
    }
}
