package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea

import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
abstract class AppRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String taskGroupName

    abstract void runProcesses();

    public long sleepTime = 600_000;

    void askToRunNewThread() throws Exception {
        Runnable r = {
            try {
                askToRun()
            } catch (Throwable e) {
                JrrUtilities.showException("Failed run ${taskGroupName}", e);
            }
        }
        new Thread(r, "${taskGroupName}").start()
    }

    void askToRun() throws Exception {
        Date startDate = new Date()
        final ObjectWrapper<Boolean> continueFlag = new ObjectWrapper<Boolean>(true);
        final ObjectWrapper<Boolean> suspendFlag = new ObjectWrapper<Boolean>(false);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date date = new Date(System.currentTimeMillis() + sleepTime);
        final JDialog dialog = new JDialog((Window) null);
        dialog.setLayout(new BorderLayout());
        dialog.setAlwaysOnTop(true);
        MyTextArea textArea = new MyTextArea(false);
        dialog.add(textArea, BorderLayout.CENTER);
        textArea.setText("${taskGroupName} task, that will be started at " + sdf.format(date));
        if (true) {
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener {
                synchronized (continueFlag) {
                    continueFlag.object = false;
                    continueFlag.notify();
                }
                log.info("${taskGroupName} ${continueFlag}");
            }
            dialog.add(cancelButton, BorderLayout.NORTH);
        }
        if (true) {
            JButton runNowButton = new JButton("Run now");
            runNowButton.addActionListener {
                synchronized (continueFlag) {
                    log.info "Run now pressed"
                    suspendFlag.object = false
                    continueFlag.object = true;
                    continueFlag.notify();

                }
                log.info("${taskGroupName} ${continueFlag} ${suspendFlag}");
            }
            dialog.add(runNowButton, BorderLayout.EAST);
        }
        if (true) {
            JButton suspend = new JButton("Suspend");
            suspend.addActionListener {
                Date d = new Date();
                textArea.setText("${taskGroupName} task suspended at ${sdf.format(d)}");
                suspendFlag.object = true
                synchronized (continueFlag) {
                    suspendFlag.object = true;
                    continueFlag.notify();
                }
                log.info("${taskGroupName} ${continueFlag} ${suspendFlag}");
            }
            dialog.add(suspend, BorderLayout.SOUTH);
        }
        dialog.setSize(200, 30);
        dialog.setLocation(300, 300);
        dialog.pack();
        SwingUtilities.invokeLater {
            dialog.setVisible(true);
        };
        boolean  suspendPress= false
        synchronized (continueFlag) {
            continueFlag.wait(sleepTime);
            if(suspendFlag.object){
                suspendPress = true;
                continueFlag.wait()
            }
        }
        log.info("${taskGroupName} ${continueFlag}");
        SwingUtilities.invokeLater {
            dialog.dispose();
        }
        long diff3 = System.currentTimeMillis() - startDate.getTime()
        log.info "${diff3} ${sleepTime}"
        if (!suspendPress && diff3 > sleepTime + 60_000) {
            log.info "${taskGroupName} stange diff ${diff3} ${sleepTime} ${new Date()}"
        } else {
            if (continueFlag.object) {
                runProcesses();
            }
        }
    }


}
