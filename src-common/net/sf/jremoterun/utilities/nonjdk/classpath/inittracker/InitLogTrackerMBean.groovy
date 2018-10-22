package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;

@CompileStatic
interface InitLogTrackerMBean {

    Vector<LogItem> getListItems()

    String getParticularLogItemStringWithException(int i)

    String getParticularLogItemString(int i)

    LogItem getParticularLogItem(int i)

    LogItem getLastLogItem()

}