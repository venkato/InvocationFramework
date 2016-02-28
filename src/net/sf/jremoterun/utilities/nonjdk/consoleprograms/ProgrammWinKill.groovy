package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import com.michaelalynmiller.jnaplatext.win32.ProcessUtils
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.PrintSelfHelp
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import net.sf.jremoterun.utilities.nonjdk.winutils.WinCmdUtils2
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
class ProgrammWinKill implements PrintSelfHelp ,ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    int pidSelf = PidDetector.detectPid();

    void k(int pid) {
        WinProcess process = WinProcessesFinder.findAllProcesses().find { it.pid == pid }
        if (process == null) {
            log.info "pid not found : ${pid}"
            System.exit(1)
        }
        log.info "killing : ${process.commandLine}"
//        String cmd = "taskkill /PID ${}"
        process.kill()
//        GeneralUtils.runNativeProcess(cmd)
        process = WinProcessesFinder.findAllProcesses().find { it.pid == pid }
        if (process != null) {
            log.info "process still alive : ${pid}"
        }
    }

    void l(String msg){
        List<WinProcess> all = WinProcessesFinder.findAllProcesses().findAll { it.pid!= pidSelf  && it.commandLine.contains(msg) };
        int size = all.size();
        log.info "found processes : ${size}"
        all.each {
            System.out.println("${it.pid} ${it.commandLine}")
        }

    }

    void killAllWindows(){
        List<ProcessUtils.ProcessInfo> windowsNotMy = WinCmdUtils2.getAllProcessedWithWindowsNotMy()
        windowsNotMy=windowsNotMy.findAll{
            String img = it.imageName;
            if(img==null||img.length()<2){
                return false
            }
            String justName  = img.tokenize('\\').last()
            switch (img){
                case {justName == 'ShellExperienceHost.exe'}:
                case {justName == 'procexp64.exe'}:
                case {justName == 'WinStore.App.exe'}:
                case 'C:\\Windows\\System32\\Taskmgr.exe':
                case 'C:\\Windows\\ImmersiveControlPanel\\SystemSettings.exe':
                case 'C:\\Windows\\explorer.exe':
                    return false
            }
            return true

        }
        windowsNotMy.each {
            println("${it.processId} ${it.imageName}")
        }
        println "Killing ${windowsNotMy.size()} processes ?"
        if(ConsoleUtils.waitConsoleYes()){
            log.info "yes done"
            windowsNotMy.each {
                try {
                    WinCmdUtils2.closeProcessSoft(it)
                }catch (Exception e){
                    log.warning("failed kill ${it.processId} ${it.imageName} : ${e}")
                }
            }
        }
    }


    void kcAll(String msg) {
        List<WinProcess> all = WinProcessesFinder.findAllProcesses().findAll { it.pid!= pidSelf  && it.commandLine.contains(msg) };
        int size = all.size();
        switch (size) {
            case 0:
                log.info "process not found :${msg}"
                System.exit(1)
                break
            case 1:
                WinProcess process = all.first()
                k(process.pid)
                break
            default:
                log.info "found many processes : ${size}"
                all.each {
                    System.out.println("${it.pid} ${it.commandLine}")
                }
                System.out.println("Killing all ?")
                if(ConsoleUtils.waitConsoleYes()){
                    all.each {k(it.pid)}
                }else {
                    log.info "do nothing"
                    System.exit(1)
                }
                break
        }

        log.info "say yes : "
        log.info "say yes2 : "
        boolean b = ConsoleUtils.waitConsoleYes()
        log.info "answer : ${b}"
    }


    void kc(String msg) {
        List<WinProcess> all = WinProcessesFinder.findAllProcesses().findAll { it.pid!= pidSelf  && it.commandLine.contains(msg) };
        int size = all.size();
        switch (size) {
            case 0:
                log.info "process not found :${msg}"
                System.exit(1)
                break
            case 1:
                WinProcess process = all.first()
                k(process.pid)
                break
            default:
                log.info "found many processes : ${size}"
                all.each {
                    System.out.println("${it.pid} ${it.commandLine}")
                }
                System.exit(1)
                break
        }
    }

    @Override
    void onMethodNotFound() {
//        log.info "nik test ${GroovyMethodRunnerParams.gmrp.args.size()}"
        if (GroovyMethodRunnerParams.gmrp.args.size() == 0) {
            printHelp()
            System.exit(1)
        }
    }

    void printHelp() {
//        AnsiConsole.systemInstall();
//        System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );
        System.out.println("k\t: kill by pid")
        System.out.println("kc\t: kill by msg contains")
        System.out.println("ka\t: kill all by msg contains")
        System.out.println("l\t: list processes by contains")
        System.out.println("killAllWindows\t: kill All Windows")
    }

}
