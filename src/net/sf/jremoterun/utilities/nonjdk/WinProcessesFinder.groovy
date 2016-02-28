package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.jvnet.winp.WinProcess;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class WinProcessesFinder {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<WinProcess> findAllProcesses(){
        List<WinProcess> all = WinProcess.all().toList();
        all = all.findAll {
            try{
                return it.getCommandLine()!=null;
            }catch (Exception e){
                return false;
            }
        }
        return all
    }




}
