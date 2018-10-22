package net.sf.jremoterun.utilities.nonjdk.awtdebug

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import sun.awt.im.InputMethodAdapter

import java.awt.AWTEvent
import java.awt.Component
import java.awt.EventQueue
import java.awt.Toolkit
import java.awt.event.InvocationEvent
import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class EventQueueDebug extends EventQueue {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static EventQueueDebug eventQueueDebug = new EventQueueDebug()

    Field ieRunnable;
    Field seqNested;
    Field inputMethod;
    Field inputMethod2;
    EvData DefaultCaret = new EvData("javax.swing.text.DefaultCaret", 10)
    EvData keyEvents = new EvData("keyEvent", 10)
    EvData mouseEvents = new EvData("mouse", 100)
    EvData globalCursorManager = new EvData("GlobalCursorManager", 100)
    EvData repaintManager = new EvData("RepaintManager", 100)
    EvData timeManager = new EvData("timeManager", 10000)
    EvData ComponentEvent = new EvData("ComponentEvent", 10)
    EvData WComponentPeer = new EvData("WComponentPeer", 10)
    EvData IgnorePaintEvent = new EvData("IgnorePaintEvent", 10)
    EvData WInputMethod = new EvData("WInputMethod", 10)

    static void setEventQueueDebug3(){
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        eventQueue.push(new EventQueueDebug());
    }

    EventQueueDebug() {
        ClassLoader cl = JrrClassUtils.currentClassLoader
        ieRunnable = JrrClassUtils.findField(InvocationEvent, 'runnable')
        inputMethod2 = JrrClassUtils.findField(sun.awt.im.InputMethodAdapter, 'clientComponent')
        seqNested = JrrClassUtils.findField(new ClRef('java.awt.SequencedEvent').loadClass(cl), 'nested')
        inputMethod = JrrClassUtils.findField(new ClRef('sun.awt.windows.WInputMethod$1').loadClass(cl), 'this$0')

    }

    @Override
    void postEvent(AWTEvent theEvent) {
        postEvent3(theEvent)
        super.postEvent(theEvent)
    }

    void postEvent3(AWTEvent theEvent) {
        String eventClassName = theEvent.getClass().getName()
        if (theEvent instanceof InvocationEvent) {
            InvocationEvent invocationEvent = (InvocationEvent) theEvent;
            Runnable data = (Runnable) ieRunnable.get(invocationEvent)
            if (data == null) {
//                println("4 null ${theEvent}")
            } else {
                String className = data.getClass().getName()
                switch (className) {
                    case { className.startsWith(winWInputMethodClass.getClassName()) }:
                        InputMethodAdapter imppl2 = (InputMethodAdapter) inputMethod.get(data)
                        Component component3 = (Component) inputMethod2.get(imppl2)
                        break
                    default:
                        break
                }
            }
        }
    }

    void printEventInfo(AWTEvent theEvent) {
        try {
            printEventInfoImpl(theEvent)
        } catch (Throwable e) {
            log.info "failed handle ${theEvent} ${e}"
            e.printStackTrace()
            System.exit(1)
        }

    }



    ClRef winWInputMethodClass = new ClRef('sun.awt.windows.WInputMethod')
    ClRef winWComponentPeerClass = new ClRef('sun.awt.windows.WComponentPeer')

    void printEventInfoImpl(AWTEvent theEvent) {
        String eventClassName = theEvent.getClass().getName()
        if (theEvent instanceof java.awt.event.KeyEvent) {
            keyEvents.newEvent()
        } else if (theEvent instanceof java.awt.event.MouseEvent) {
            mouseEvents.newEvent();
        } else if (theEvent instanceof sun.awt.event.IgnorePaintEvent) {
            IgnorePaintEvent.newEvent();
        } else if (theEvent instanceof java.awt.event.ComponentEvent) {
            ComponentEvent.newEvent();
        } else if (eventClassName == 'java.awt.SequencedEvent') {
            def get = seqNested.get(theEvent)
            println("5 ${theEvent} ${get}")
        } else if (theEvent instanceof InvocationEvent) {
            InvocationEvent invocationEvent = (InvocationEvent) theEvent;
            Runnable data = (Runnable) ieRunnable.get(invocationEvent)
            if (data == null) {
                println("4 null ${theEvent}")
            } else {
                String className = data.getClass().getName()
                switch (className) {
                    case { className.startsWith(javax.swing.RepaintManager.getName()) }:
                        repaintManager.newEvent();
                        break;
                    case { className.startsWith(javax.swing.Timer.getName()) }:
                        timeManager.newEvent();
                        break;
                    case { className.startsWith(javax.swing.text.DefaultCaret.getName()) }:
                        DefaultCaret.newEvent();
                        break;
                    case { className.startsWith(sun.awt.GlobalCursorManager.getName()) }:
                        globalCursorManager.newEvent();
                        break;
                    case { className.startsWith(winWComponentPeerClass.getClassName()) }:
                        WComponentPeer.newEvent();
                        break;
                    case { className.startsWith(winWInputMethodClass.getClassName()) }:
                        InputMethodAdapter imppl2 = (InputMethodAdapter) inputMethod.get(data)
                        Component component3 = (Component) inputMethod2.get(imppl2)
                        if (component3 == null) {
                            println("got WInputMethod with null")
                            WInputMethod.newEvent();
                        } else {
                        }
                        break;
                    default:
                        println("1 " + data.getClass().getName())
                        break;

                }
            }
        } else {
            println("2 " + eventClassName)
        }
    }

    @Override
    protected void dispatchEvent(AWTEvent event) {
        printEventInfo(event)
        super.dispatchEvent(event)
    }
}
