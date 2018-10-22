package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities

import javax.management.MBeanServer
import javax.management.ObjectName
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class InitLogTracker implements InitLogTrackerMBean{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static  ObjectName defaultObjectName = new ObjectName('iff:type=initlogs')
    public static InitLogTracker defaultTracker = new InitLogTracker();
    public Vector<LogItem> listItems = new Vector<>()
    //public boolean passToLog = false;
    public boolean passToSysout = false;

    static {
        JrrClassUtils.addIgnoreClass(JrrClassUtils.getCurrentClass())
    }

    InitLogTracker() {
        try {
            MBeanServer beanServer = JrrUtils.findLocalMBeanServer();
            if (!beanServer.isRegistered(defaultObjectName)) {
                beanServer.registerMBean(this, defaultObjectName)
            }
        }catch(Exception e){
            log.log(Level.SEVERE,"failed register initLogTracker",e);
        }
    }

    void setListItems(Vector<LogItem> listItems) {
        this.listItems = listItems
    }

    void addLog(String msg){
        LogItem logItem =new LogItem()
        logItem.msg = msg
        listItems.add(logItem)
        if(passToSysout){
            println(msg)
        }
    }

    void addException(String msg,Throwable exception){
        LogItem logItem =new LogItem()
        logItem.msg = msg
        logItem.exception = exception
        listItems.add(logItem)
        println("${msg} ${exception}")
    }



    @Override
    Vector<LogItem> getListItems() {
        return listItems
    }

    @Override
    String getParticularLogItemStringWithException(int i){
        return JrrUtils.exceptionToString(getParticularLogItem(i).exception)
    }

    @Override
    String getParticularLogItemString(int i){
        return getParticularLogItem(i).toString()
    }

    @Override
    LogItem getParticularLogItem(int i){
        return listItems.get(i)
    }

    @Override
    LogItem getLastLogItem(){
        return listItems.lastElement();
    }

}
