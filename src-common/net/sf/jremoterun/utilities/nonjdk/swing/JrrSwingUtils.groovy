package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.ObjectWrapper

import javax.swing.*
import java.awt.Component
import java.awt.Window
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.concurrent.Callable
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class JrrSwingUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void moveAllVisibleWindowToLeftUpperCorner() {
        moveAllVisibleWindowTo(10, 10);
    }

    static void moveAllVisibleWindowTo(int x, int y) {
        invokeNowOrLaterInSwingThread {
            Collection<Window> windows = JrrUtilities.findVisibleAwtWindows();
            windows.each { it.setLocation(x, y) }
        }
    }

    static void invokeAndWaitInSwingThread(Callable callable) {
        if (SwingUtilities.isEventDispatchThread()) {
            callable.call()
        } else {
            ObjectWrapper<Throwable> exc = new ObjectWrapper<>(null);
            SwingUtilities.invokeAndWait {
                try {
                    callable.call()
                } catch (Throwable e) {
                    exc.object = e;
                }
            };
            if (exc.object != null) {
                Throwable e = exc.object;
                JrrUtils.throwThrowable(e);
            }
        }
    }


    static void invokeNowOrLaterInSwingThread(Callable callable) {
        assert callable != null
        if (SwingUtilities.isEventDispatchThread()) {
            callable.call()
        } else {
//            Exception stackTrace = new Exception("stackTrace")
            SwingUtilities.invokeLater {
                try {
                    callable.call()
                } catch (Throwable e) {
//                    def string = JrrUtils.exceptionToString(stackTrace)
                    log.log(Level.SEVERE, "fail call invoke later : ${callable.getClass().getName()}", e);
                    StackTraceElement[] trace = e.getStackTrace()
                    if (trace == null || trace.length == 0) {
                        log.severe("empty exception stack : ${e} ${callable.getClass().getName()}")
                    }
                }
            };
        }
    }

    static void tranferFocus(Component from, Component to) {
        from.addFocusListener(new FocusListener() {
            @Override
            void focusGained(FocusEvent e) {
                to.requestFocus();
            }

            @Override
            void focusLost(FocusEvent e) {

            }
        }

        );
    }


}
