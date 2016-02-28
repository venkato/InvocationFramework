package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.AppRunner
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
class ProgramRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public void f1(ProgramInfo programInfo) {
        if (checkProcessRunning(programInfo)) {
            log.info "program running : ${programInfo.name()}"
        } else {
            AppRunner appRunner = new AppRunner() {
                @Override
                void runProcesses() {
                    if (checkProcessRunning(programInfo)) {
                        log.info "program seems already running : ${programInfo.name()} , manually started ?"
                    } else {
                        programInfo.runProcess();
                    }
                }
            }
            appRunner.taskGroupName = programInfo.name();
            appRunner.askToRunNewThread();
        }
    }

    boolean checkProcessRunning(ProgramInfo contains) {
        List<WinProcess> listOfProcesses = WinProcessesFinder.findAllProcesses()
        List<WinProcess> processes = listOfProcesses.findAll { contains.matches(it.commandLine) }
        if (processes.size() > 1) {
            throw new Exception("too many processes for ${contains}  ${processes.size()} ${processes.collect { it.commandLine }})}")
        }
        return processes.size() == 1

    }

}
