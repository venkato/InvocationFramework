package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LogCounter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public volatile int currentCount;
    public int countBy;
    public int countBy10;
    public int countBy100;
    public int countBy1000;
    public int countBy10000;
    public int countBy100000;
    public int countBy1000000;
    public int countBy10000000;
    public int countBy100000000;



    LogCounter() {
        this(3)
    }

    LogCounter(int countBy) {
        assert countBy < 10;
        this.countBy = countBy
        countBy10 = countBy * 10;
        countBy100 = countBy * 100;
        countBy1000 = countBy * 1000;
        countBy10000 = countBy * 10_000;
        countBy100000 = countBy * 100_000;
        countBy1000000 = countBy * 1_000_000;
        countBy10000000 = countBy * 10_000_000;
        countBy100000000 = countBy * 100_000_000;
    }

    boolean isNeedLog2(int count, int numm) {
        if (count % numm == 0) {
            return true
        }
        return false;

    }

    void increaseCount(){
        currentCount++
    }

    boolean isNeedLog3() {
        return isNeedLog(currentCount)
    }

    boolean isNeedLog(int count) {
        if (count < 10) {
            return count < countBy;
        }
        if (count < countBy10) {
            return isNeedLog2(count, 10)
        }
        if (count < countBy100) {
            return isNeedLog2(count, 100)
        }
        if (count < countBy1000) {
            return isNeedLog2(count, 1_000)
        }
        if (count < countBy10000) {
            return isNeedLog2(count, 10_000)
        }
        if (count < countBy100000) {
            return isNeedLog2(count, 100_000)
        }

        if (count < countBy1000000) {
            return isNeedLog2(count, 1_000_000)
        }
        if (count < countBy10000000) {
            return isNeedLog2(count, 10_000_000)
        }
        if (count < countBy100000000) {
            return isNeedLog2(count, 100_000_000)
        }

    }


    public static LogCounter logCounterDefault = new LogCounter()

}
