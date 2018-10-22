package net.sf.jremoterun.utilities.nonjdk.memorystat;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.MemoryUsage;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MemoryInstanceInfoGG {
    // can't be ser due to MemoryUsage not ser


    String name;
    MemoryUsage before;
    MemoryUsage after;
    float freePercent;
    float diffToNowPercent;


}
