package tcpmon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class extMethodTester implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        // used in
        new ClRef('net.sf.jremoterun.utilities.nonjdk.groovy.ExtentionMethodChecker2')

        log.debug("test msg");
        File f1 = new File('.');
        File f2 = new File('.');
        f1.isChildFile(f2);
    }
}
