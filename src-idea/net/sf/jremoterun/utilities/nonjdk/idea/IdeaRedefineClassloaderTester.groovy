package net.sf.jremoterun.utilities.nonjdk.idea;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaRedefineClassloaderTester implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        IdeaRedefineClassloader.ideaPluginId = 'randomid'
        IdeaRedefineClassloader.redifineClassloader();
        RedefineIdeaClassUtils.ideaLoggerTurnOff()
    }
}
