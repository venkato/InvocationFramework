package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper2
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorI
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorIThrowImmediate
import org.apache.logging.log4j.LogManager

import java.util.logging.Logger

@CompileStatic
public class IdeaClassPathTester extends ClassPathTesterHelper2 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    IdeaClassPathTester(ProblemCollectorI problemCollector) {
        super(problemCollector)
    }

    IdeaClassPathTester() {
        super(new ProblemCollectorIThrowImmediate())
    }

    @Override
    void run() {
        runImpl()
    }

    void runImpl() {
        CheckNonCache2.check();
        checkNoSuchClass new ClRef( "org.apache.log4j.Logger");
        checkNoSuchClass new ClRef("org.apache.commons.logging.LogFactory");

        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger("test");
        checkClassInstanceOf5(logger2, org.apache.logging.log4j.core.Logger.class);

        checkNoSuchClass new ClRef("org.slf4j.LoggerFactory");
        checkNoSuchClass new ClRef("org.slf4j.Logger");
        checkNoSuchClass new ClRef("sun.jvmstat.monitor.HostIdentifier");
        checkNoSuchClass new ClRef("com.sun.jna.Native");

    }


}
