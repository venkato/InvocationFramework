package net.sf.jremoterun.utilities.nonjdk.audio.idea

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.IdeaAddFileWithSources
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.LibManager3;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.nonjdk.audio.JrrText2Speech

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class IdeaClasspathImportFailed implements NewValueListener<Throwable> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void newValue(Throwable throwable) {
        try {
            JrrUtilities.showException('Classpath imported failed', throwable)
            String msg = 'Classpath imported failed : ' + throwable;
            if(msg.length()>100){
                msg = msg.substring(0,99)
            }
            JrrText2Speech.playTextOnAllDevicesS(msg)
        } catch (Throwable e) {
            log.log(Level.SEVERE,"failed play audio",e)
            JrrUtilities.showException('failed play audio', e)
        }
    }


    static void init1(){
        marytts.util.MaryRuntimeUtils.ensureMaryStarted()
        LibManager3.importFinishedFine = new IdeaClasspathImportedFine();
        IdeaClasspathImportFailed importFailed = new IdeaClasspathImportFailed()
        IdeaAddFileWithSources.importFailed = importFailed;
        LibManager3.importFailed = importFailed;
    }
}
