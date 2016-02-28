package idea.plugins.thirdparty.filecompletion.share.Ideasettings;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MoveFileDialogSettings {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public  static String defaultPackageName = ""
    public static String defaultSimpleClassName=""
    public static String defaultParentFileVar=""
}
