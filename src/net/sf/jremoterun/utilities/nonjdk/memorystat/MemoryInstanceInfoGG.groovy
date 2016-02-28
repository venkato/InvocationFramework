package net.sf.jremoterun.utilities.nonjdk.memorystat;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.MemoryUsage;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MemoryInstanceInfoGG {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    String name;
    MemoryUsage before;
    MemoryUsage after;
    float freePercent;
    float diffToNowPercent;


}
