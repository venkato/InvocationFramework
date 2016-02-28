package net.sf.jremoterun.utilities.nonjdk.winutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils

import java.util.logging.Logger

@CompileStatic
class WinCmdUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void stopService(String serviceName) {
        String cmd = " sc config ${serviceName} start= disabled"
        GeneralUtils.runNativeProcess(cmd)
        cmd = " sc stop ${serviceName}"
        GeneralUtils.runNativeProcess(cmd)

    }


}
