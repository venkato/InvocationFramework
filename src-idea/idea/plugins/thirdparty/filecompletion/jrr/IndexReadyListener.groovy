package idea.plugins.thirdparty.filecompletion.jrr

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.Scheduler

import javax.swing.SwingUtilities
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@CompileStatic
class IndexReadyListener {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<Runnable> listenersAfterProjectOpened = []
    public static List<Runnable> listenersAfterIndexReady = []

    public static volatile projectOpened = false
    public static volatile indexReady = false
    public static volatile runnerInProgress = false


    static void addListenerAfterProjectOpened(Runnable r) {
        assert !projectOpened
        listenersAfterProjectOpened.add(r)
    }


    static void addListenerAfterIndexReady(Runnable r) {
        assert !indexReady
        listenersAfterIndexReady.add(r)
    }

    static void runListenersWhenReady() {
        if (runnerInProgress) {

        } else {
            runnerInProgress = true
            SwingUtilities.invokeLater {
                try {
                    Runnable r = {
                        try {
                            check1()
                        } catch (Throwable e) {
                            JrrUtilities.showException("failed run", e)
                        }
                    }
                    Thread thread = new Thread(r, "Open idea waiter")
                    thread.start()
                } catch (Throwable e) {
                    JrrUtilities.showException("failed run", e)
                }
            }
        }
    }

    static int maxCount = 10

    static void check1() {
        int i = maxCount
        while (i > 0) {
            log.info "check ${i}"
            if (isAtLeastOneProjectOpened()) {
                SwingUtilities.invokeLater {
                    try {
                        List<Runnable> l2 = new ArrayList<>(listenersAfterProjectOpened)
                        l2.add { waitIndexReady() }
                        projectOpened = true
                        runListeners3(l2)
                    } catch (Throwable e) {
                        JrrUtilities.showException("failed run", e)
                    }
                }
                return
            }
            Thread.sleep(30_000)
            i--;
        }
        log.info "no one prject still opened at ${new Date()}"
    }


    static void runListeners3(List<Runnable> listeners) {
        listeners = new ArrayList<>(listeners);
        if (listeners.size() == 0) {
            log.info "all listeners started"
        } else {
            Runnable first = listeners.first();
            listeners.remove(0)
            SwingUtilities.invokeLater {
                try {
                    first.run();
                    runListeners3(listeners)
                } catch (Throwable e) {
                    JrrUtilities.showException("failed run ${it}", e)
                }
            }
        }
    }


    static void waitIndexReady() {
        DumbService.getInstance(getOpenedProject()).smartInvokeLater {
            Scheduler.scheduleOnceInSwingThread(30, TimeUnit.SECONDS) {
                DumbService.getInstance(getOpenedProject()).smartInvokeLater {
                    log.info "Running on index ready ${new Date()}"
                    indexReady = true
                    runListeners3(listenersAfterIndexReady)
                }
            }
        }
    }


    static boolean isAtLeastOneProjectOpened() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects()
        return openProjects != null && openProjects.length > 0
    }

    static Project getOpenedProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects == null || openProjects.length == 0) {
            throw new IllegalStateException("Can't find open project");
        }
        return openProjects[0];

    }

}
