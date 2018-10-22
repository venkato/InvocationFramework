package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean;
import net.sf.jremoterun.utilities.nonjdk.AppRunner
import net.sf.jremoterun.utilities.nonjdk.InitGeneral
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle

import javax.management.ObjectName
import java.util.logging.Logger;

@CompileStatic
abstract class RunProgramOnStart2 {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AdjustPeriodTimer adjustPeriodTimer

    ProgramRunner programRunner = new ProgramRunner();

    public static volatile long logProcessRunningInSec = 3600*6;
    /**
     * delay in ms
     */
    int runProcessedOnStartDelay = 60_000


    void runProgramsFromTimer() {
        log.info("start checking from timer");
        ProgramRunner programRunner = new ProgramRunner();
        for (ProgramInfo homeProgram : getMonitoredProcesses()) {
            programRunner.startProcessIfNeeded(homeProgram);
        }
        log.info("finish check from timer");
    }

    /**
     * @param period in milli seconds
     * @throws Exception
     */
    void runProgramOnStart3(long period) throws Exception {
        runProgramOnStart()
        adjustPeriodTimer = new AdjustPeriodTimer(period, TimerStyle.Consecutive, {
            runProgramsFromTimer();
        })
        adjustPeriodTimer.start2()
        MBeanFromJavaBean.registerMBean(adjustPeriodTimer, new ObjectName("jrrutils:timer=apprunner"))
    }


    void runProgramOnStart() throws Exception {
        InitGeneral.init1();
        AppRunner appRunner = new AppRunner() {
            @Override
            void runProcesses() {
                for (ProgramInfo programInfo : getMonitoredProcesses()) {
                    checkProgramOnStart(programInfo)
                }
            }
        };
        appRunner.taskGroupName = "Run process on start";
        appRunner.sleepTime = runProcessedOnStartDelay;
        appRunner.askToRunNewThread();
    }

    void checkProgramOnStart(ProgramInfo programInfo) {
        if (programRunner.checkProcessRunning(programInfo)) {
            log.info("program running : " + programInfo);
        } else {
            programInfo.runProcess();
        }

    }

    static Thread defaultProcessRunnerInNewThread(ProgramInfo programInfo, File genericLogDir, int rotationDepth) {
        return new RunProgramOnStart3(programInfo,genericLogDir,rotationDepth).defaultProcessRunnerInNewThread()
    }



    abstract List<ProgramInfo> getMonitoredProcesses();

}
