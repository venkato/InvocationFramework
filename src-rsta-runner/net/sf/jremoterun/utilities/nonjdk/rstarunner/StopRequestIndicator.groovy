package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class StopRequestIndicator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ThreadLocal<RstaRunner> stopRequest = new ThreadLocal<>()

}
