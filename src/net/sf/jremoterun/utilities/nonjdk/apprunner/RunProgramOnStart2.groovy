package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean;
import net.sf.jremoterun.utilities.nonjdk.AppRunner;
import net.sf.jremoterun.utilities.nonjdk.InitGeneral
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle

import javax.management.ObjectName
import java.util.logging.Logger;

@CompileStatic
abstract class RunProgramOnStart2 {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int jmxPort = 7652;


    void runProgramsFromTimer() {
        log.info("start checking from timer");
        ProgramRunner programRunner = new ProgramRunner();
        for (ProgramInfo homeProgram : getMonitoredProcesses()) {
            programRunner.f1(homeProgram);
        }
        log.info("finish check from timer");
    }

    void runProgramOnStart3(long period) throws Exception {
        runProgramOnStart()
//        Scheduler.scheduleOnceInAnyThread(10,TimeUnit.MINUTES,{
        AdjustPeriodTimer adjustPeriodTimer = new AdjustPeriodTimer(period, TimerStyle.Consecutive, {
            runProgramsFromTimer();
        })
        adjustPeriodTimer.start2()
//        });
        MBeanFromJavaBean.registerMBean(adjustPeriodTimer, new ObjectName("jrrutils:timer=apprunner"))
//        JrrTimerTask2 jrrTimerTask2 =new JrrTimerTask2();
//        jrrTimerTask2.


    }



    void runProgramOnStart() throws Exception {
        JrrUtils.creatJMXConnectorAndRMIRegistry(null,jmxPort,null,null);
        InitGeneral.init1();
        AppRunner appRunner = new AppRunner() {
            @Override
            public void runProcesses() {
                ProgramRunner programRunner = new ProgramRunner();
                for (ProgramInfo homeProgram : getMonitoredProcesses()) {
                    if (programRunner.checkProcessRunning(homeProgram)) {
                        log.info("program running : " + homeProgram);
                    } else {
                        homeProgram.runProcess();
                    }

                }
            }
        };
        appRunner.taskGroupName = "Run process on start";
        appRunner.sleepTime = 150_000;
        appRunner.askToRunNewThread();
    }

    abstract List<ProgramInfo> getMonitoredProcesses();

}
