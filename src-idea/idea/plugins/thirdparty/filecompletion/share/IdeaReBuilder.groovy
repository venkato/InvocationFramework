package idea.plugins.thirdparty.filecompletion.share

import com.intellij.history.LocalHistory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompileStatusNotification
import com.intellij.openapi.compiler.CompilerBundle
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.compiler.JavaCompilerBundle
import com.intellij.openapi.project.Project
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.ObjectWrapper

import javax.swing.SwingUtilities;
import java.util.logging.Logger;

@CompileStatic
class IdeaReBuilder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final Project project
    private final Object lock = new Object()
    public volatile boolean aborted2 = false;
    public volatile int errors2 = -1;

    public volatile Throwable exception;
    public final int maxWait = 3600_000


    IdeaReBuilder() {
        project = OSIntegrationIdea.getOpenedProject()
    }

    void rebuild() {
        synchronized (lock) {
            SwingUtilities.invokeLater {
                try {
                    rebuildImpl()
                } catch (Throwable e) {
                    exception = e
                    log.warn("build failed", e);
                    JrrUtilities.showException("build failed", e);
                    resultReady()
                }

            }
            lock.wait(maxWait)
        }
        if(exception!=null){
            Exception e = new Exception(exception.toString());
            e.setStackTrace(exception.getStackTrace());
            throw e;
        }
        log.info "errors : ${errors2} , aborted : ${aborted2}"
        if (aborted2) {
            throw new Exception("aborted")
        }
        if (errors2 > 0) {
            throw new Exception("errors : ${errors2}")
        }
    }

    private void resultReady() {
        synchronized (lock) {
            lock.notifyAll()
        }
    }

    void rebuildImpl() {
        Runnable rebuildingImplR = {
            try {
                log.info "rebuilding"
                CompilerManager.getInstance(project).rebuild(new CompileStatusNotificationMy());
                log.info "rebuild all done"
            } catch (Throwable e) {
                exception = e
                log.warn("build failed", e);
                JrrUtilities.showException("build failed", e);
                resultReady()
            }
        }

        Runnable saveAll = {
            try {
                log.info("saving project ..")
                OSIntegrationIdea.saveAllImpl2()
                log.info("project saved")
                JrrIdeaUtils.submitTr(rebuildingImplR)
            } catch (Throwable e) {
                exception = e;
                resultReady()
                throw e;
            }
        }
        Runnable refreshImpl = {
            try {
                log.info "refreshing all ..."
                OSIntegrationIdea.refreshImpl()
                log.info "refreshing all done"
                JrrIdeaUtils.submitTr(saveAll)
            } catch (Throwable e) {
                exception = e;
                resultReady()
                throw e;
            }
        }
        JrrIdeaUtils.submitTr refreshImpl
    }


    class CompileStatusNotificationMy implements CompileStatusNotification {
        @Override
        public void finished(boolean aborted, int errors, int warnings,
                             final CompileContext compileContext) {
            aborted2 = aborted
            errors2 = errors;
            if (aborted || project.isDisposed()) {
                return;
            }

            String text = "";
            LocalHistory.getInstance().putSystemLabel(project, errors == 0
                    ? JavaCompilerBundle.message("rebuild.lvcs.label.no.errors", text)
                    : JavaCompilerBundle.message("rebuild.lvcs.label.with.errors", text));
            log.info "compilation finished"
            resultReady()
        }
    }

}
