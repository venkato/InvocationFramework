package net.sf.jremoterun.utilities.nonjdk.idea.init;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaClassPathSettings {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String customScriptProperty = "idea.custom.init"
    public static String pluginCLassloaderId = "pluginclassloaderId"
}
