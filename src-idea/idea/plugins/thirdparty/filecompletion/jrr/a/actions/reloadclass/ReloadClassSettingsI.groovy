package idea.plugins.thirdparty.filecompletion.jrr.a.actions.reloadclass

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.redefineclass.RedefineClassI;

import java.util.logging.Logger;

@CompileStatic
interface ReloadClassSettingsI {

    RedefineClassI receiveConnection();

    String getClassLoaderId();

}
