package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.FileQuickInfoActionImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class FileQuickInfoAction extends DeligateAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    public static DeligateAction thisObject

    public FileQuickInfoAction() {
        super(new FileQuickInfoActionImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
