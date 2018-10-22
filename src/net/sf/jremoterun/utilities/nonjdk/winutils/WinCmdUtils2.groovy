package net.sf.jremoterun.utilities.nonjdk.winutils

import com.michaelalynmiller.jnaplatext.win32.ProcessUtils
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.util.logging.Logger

@CompileStatic
class WinCmdUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static List<ProcessUtils.ProcessInfo> getAllProcessedWithWindowsNotMy() {
        List<ProcessUtils.ProcessInfo> list = ProcessUtils.getProcessList()
        List<ProcessUtils.ProcessInfo> ancestors = ProcessUtils.getProcessAncestors();
        List<Integer> processes = ancestors.collect { it.processId }
        list = list.findAll { !processes.contains(it.processId) }
        list = list.findAll {
            finda(it.processId).size() > 0
        }
        return list
    }

    static void closeProcessSoft(ProcessUtils.ProcessInfo processId) {
        String cmd = "taskkill /PID ${processId.processId}"
        NativeProcessResult.runNativeProcessAndWait cmd
        log.info "close event sent for ${processId.processId} ${processId.imageName}"
    }


    static List<WinDef.HWND> finda(int processId) {
//        List<WinDef.HWND> result = []
        List<WinDef.HWND> windows = ProcessUtils.getProcessWindows(processId)
        windows = windows.findAll { User32.INSTANCE.IsWindowVisible(it) && User32.INSTANCE.IsWindowEnabled(it) }
//        if (windows.size() > 1) {
//            log.info "found many for ${processId} ${windows.size()}"
//            windows = windows.findAll {
//                List<WinDef.HWND> childs = []
//                User32.INSTANCE.EnumChildWindows(it, new WinUser.WNDENUMPROC() {
//                    @Override
//                    boolean callback(WinDef.HWND hWnd, Pointer data) {
//                        childs.add(hWnd)
//                        return true
//                    }
//                }, null);
//                return childs.size() > 0
//            }
//            if (windows.size() == 1) {
//                log.info "found top window for ${processId}"
//            } else {
//                log.info "faile dfind root for ${processId} ${windows.size()}"
//            }
//        } else {
//        }
        return windows

    }
}
