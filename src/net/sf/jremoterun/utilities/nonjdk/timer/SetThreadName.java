package net.sf.jremoterun.utilities.nonjdk.timer;

import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

import groovy.transform.CompileStatic;

@CompileStatic
public class SetThreadName {

    public static void setThreadName(Thread thread,String threadName){
        thread.setName(threadName);
    }

}
