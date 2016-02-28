package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.event.IvyEvent
import org.apache.ivy.core.event.IvyListener
import org.apache.ivy.core.event.download.PrepareDownloadEvent
import org.apache.ivy.core.event.resolve.EndResolveEvent;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IdeaIvyEvent implements IvyListener{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    IdeaClasspathLongTaskInfo longTaskInfo;

    IdeaIvyEvent(IdeaClasspathLongTaskInfo longTaskInfo) {
        this.longTaskInfo = longTaskInfo
    }

    @Override
    void progress(IvyEvent event) {
        String msg;
        switch (event){
            case {event instanceof PrepareDownloadEvent}:
                PrepareDownloadEvent event2 = event as PrepareDownloadEvent

                String e = event2.artifacts.collect { it.toString() }.join(' ')
                msg = "prepare download : ${e}"
                break;
            case {event instanceof EndResolveEvent}:
                EndResolveEvent event2 = event as EndResolveEvent

                String e = event2.moduleDescriptor.moduleRevisionId.toString()
                msg = "downloaded : ${e}"
                break;
            default:
                msg =  "${event} ${event.class.name}"

        }
        log.info "${msg}"
        longTaskInfo.setCurrentTask(msg)
    }

}
