package idea.plugins.thirdparty.filecompletion.jrr

import com.intellij.diagnostic.LoadingState
import com.intellij.diagnostic.StartUpMeasurer
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.Scheduler

import javax.swing.SwingUtilities
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IndexReadyListener {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<Runnable> listenersAfterProjectOpened = []
    public static List<Runnable> listenersAfterIndexReady = []

    public static volatile projectOpened = false
    public static volatile indexReady = false
    public static volatile runnerInProgress = false
    public static ClassLoader classLoaderWrap = JrrClassUtils.getCurrentClassLoader();

    public static int maxCount2 = 20;
    public static long sleepTime2 = 10_000;


    static void addListenerAfterProjectOpened(Runnable r) {
        assert !projectOpened
        listenersAfterProjectOpened.add(r)
    }


    static void addListenerAfterIndexReady(Runnable r) {
        assert !indexReady
        listenersAfterIndexReady.add(r)
    }

    static void runListenersWhenReady2() {
        if (runnerInProgress) {

        } else {
            try {
                Runnable r = {
                    try {
                        check2()
                    } catch (Throwable e) {
                        JrrUtilities.showException("failed run", e)
                    }
                }
                Thread thread = new Thread(r, "Open idea waiter2")
                thread.setContextClassLoader(classLoaderWrap);
                thread.start()
            } catch (Throwable e) {
                JrrUtilities.showException("failed run", e)
            }

        }
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
                            log.log(Level.SEVERE,"failed run", e)
                            JrrUtilities.showException("failed run", e)
                        }
                    }
                    Thread thread = new Thread(r, "Open idea waiter")
                    thread.setContextClassLoader(classLoaderWrap);
                    thread.start()
                } catch (Throwable e) {
                    JrrUtilities.showException("failed run", e)
                }
            }
        }
    }


    public static int maxCount1 = 10;

    public static Runnable waitTimeout1 = {
        JrrUtilities.showException("no one project still opened 1",new Exception("no one project still opened at ${new Date()}"));
    };

    static void check1() {
        int i = maxCount1
        while (i > 0) {
            boolean projectOpened =isAtLeastOneProjectOpened()
            log.info "check ${i} projectOpened = ${projectOpened}"
            if (projectOpened) {
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
        log.info "no one prject still opened 1 at ${new Date()}"
        waitTimeout1.run()
    }



    public static Runnable waitTimeout2 = {
        JrrUtilities.showException("no one project still opened 2",new Exception("no one project still opened at ${new Date()}"));
    };

    static void check2() {
        final AtomicReference<LoadingState> state = JrrClassUtils.getFieldValue(StartUpMeasurer,'currentState') as AtomicReference<LoadingState>
        int i = maxCount2
        while (i > 0) {

            LoadingState state1 = state.get()
            log.info "check2 ${i} current state = ${state1}"
            if( state1!=null && state1.ordinal() >= LoadingState.APP_STARTED.ordinal()){
                log.info "app started ${state1}"
                runListenersWhenReady()
                return
            }
            Thread.sleep(sleepTime2)
            i--;
        }
        log.info "no one project still opened 2 at ${new Date()}"
        waitTimeout2.run()
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
                    ContextClassLoaderWrapper.wrap2(classLoaderWrap,first)
                    runListeners3(listeners)
                } catch (Throwable e) {
                    JrrUtilities.showException("failed run ${first}", e)
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
