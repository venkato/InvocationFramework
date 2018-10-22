package net.sf.jremoterun.utilities.nonjdk.memorystat

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.management.MemoryPoolMXBean;
import java.util.logging.Logger;

@CompileStatic
class MemoryInfoBean {

    String name;
    MemoryPoolMXBean nativeBean;

    long peek
    long used
    long max
    float usedPercent

    @Override
    String toString() {
        return name
    }
}
