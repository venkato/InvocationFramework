package net.sf.jremoterun.utilities.nonjdk.audio.idea

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.audio.JrrText2Speech

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class IdeaClasspathImportedFine implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        try {
            JrrText2Speech.playTextOnAllDevicesS('Classpath imported to Idea fine')
        }catch(Throwable e){
            log.log(Level.SEVERE,"failed play audio",e)
            JrrUtilities.showException('failed play audio',e)
        }
    }
}
