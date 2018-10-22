package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.application.ex.ApplicationInfoEx
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class CurrentIdeaVersionUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String findCurrentIdeaVersion() {
        ApplicationInfoEx appInfo = ApplicationInfoEx.getInstanceEx()
        return appInfo.getFullVersion()
    }


    static String findCurrentIdeaVersionExtended() {
        ApplicationInfoEx appInfo = ApplicationInfoEx.getInstanceEx()
        int[] components = appInfo.getBuild().getComponents()
        return components.toList().join('.')
    }



}
