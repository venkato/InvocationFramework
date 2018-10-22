package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LogItem implements Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public String location;
    public String msg;
    public Throwable exception;

    @Override
    String toString() {
        if(exception==null){
            return msg;
        }
        return "${msg} ${exception}"
    }
}
