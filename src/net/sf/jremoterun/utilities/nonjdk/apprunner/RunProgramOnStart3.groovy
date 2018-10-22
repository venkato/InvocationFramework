package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.nonjdk.AppRunner
import net.sf.jremoterun.utilities.nonjdk.InitGeneral
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle

import javax.management.ObjectName
import java.util.logging.Logger

@CompileStatic
class RunProgramOnStart3 {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProgramInfo programInfo;
    public File genericLogDir;
    public int rotationDepth;
    public NativeProcessResult processResult;
    public Thread thread
    public List<String> env = NativeProcessResult.defaultEnv;

    RunProgramOnStart3(ProgramInfo programInfo, File genericLogDir, int rotationDepth) {
        this.programInfo = programInfo
        this.genericLogDir = genericLogDir
        this.rotationDepth = rotationDepth
    }

    void defaultProcessRunnerInNewThread() {
        Runnable r = {
            defaultProcessRunner()
        }
        thread = new Thread(r, "Jrr app runner for ${programInfo}")
        thread.start()
        thread.join(1000)
    }

    void defaultProcessRunner() {
        log.info("running " + programInfo);
        File child = genericLogDir.child(programInfo.name())
        child.mkdir()
        assert child.exists()
        File outFile = child.child('out.txt')
        //NativeProcessResult.runNativeProcessAndWait(programInfo.getRunFile().absolutePath, programInfo.getRunFile().getParentFile())
        runProcessImpl()
        //processResult.timeoutInSec = logProcessRunningInSec;
        processResult.addWriteOutToFile(outFile,rotationDepth)
        processResult.waitAsyncM()
        //GeneralUtils.runNativeProcessRedirectOutputToFile(programInfo.getRunFile().absolutePath, programInfo.getRunFile().getParentFile(), false, outFile, rotationDepth);
        log.info("started : " + programInfo);
    }

    void runProcessImpl(){
        Process process = programInfo.getRunFile().getAbsolutePath().execute(env, programInfo.getRunFile().getParentFile());
        processResult = new NativeProcessResult(process)
    }


}
