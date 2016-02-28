package net.sf.jremoterun.utilities.nonjdk.awtdebug

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class EvData implements Serializable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
    String name;
    long total
    int current;
    volatile int max;
    long lastLimit = System.currentTimeMillis();

    EvData(String name, int max) {
        this.name = name
        this.max = max
    }

    void newEvent() {
        total++;
        current++;
        if (current > max) {
            long timeNow = System.currentTimeMillis()
            long diff = timeNow - lastLimit
            BigDecimal diff2 = diff / 1000
            Date date = new Date(timeNow)
            println("${sdf.format(date)} - got ${name} events : ${current}, total : ${total}, diff = ${diff2} sec")
            lastLimit = timeNow
            current = 0;
        }
    }
}
