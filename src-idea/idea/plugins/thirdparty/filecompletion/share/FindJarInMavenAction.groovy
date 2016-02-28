package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.FindJarFileInMavenActionImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class FindJarInMavenAction extends DeligateAction {
    private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);


    public static DeligateAction thisObject

    public FindJarInMavenAction() {
        super(new FindJarFileInMavenActionImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
