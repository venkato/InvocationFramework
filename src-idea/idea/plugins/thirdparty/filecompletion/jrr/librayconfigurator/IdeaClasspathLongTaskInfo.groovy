package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.progress.ProgressIndicator
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Logger

@CompileStatic
class IdeaClasspathLongTaskInfo extends LongTaskInfo {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ProgressIndicator indicator;

    IdeaClasspathLongTaskInfo(ProgressIndicator indicator) {
        this.indicator = indicator
    }

    @Override
    void setCurrentTask(String taskName) {
        super.setCurrentTask(taskName)
        indicator.setText(taskName)
    }

    @Override
    boolean getCancelled() {
        boolean isCan = super.getCancelled()
        if (isCan) {
            return true
        }
        return indicator.isCanceled()

    }
}
