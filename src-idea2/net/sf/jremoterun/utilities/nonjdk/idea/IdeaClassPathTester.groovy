package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper
import org.apache.logging.log4j.LogManager

import java.util.logging.Logger

@CompileStatic
public class IdeaClassPathTester extends ClassPathTesterHelper {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public void runImpl() throws Exception {
        CheckNonCache2.check();
        checkNoSuchClass(new ClRef( "org.apache.log4j.Logger"));
        checkNoSuchClass new ClRef("org.apache.commons.logging.LogFactory");

        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger("test");
        checkClassInstanceOf(logger2, org.apache.logging.log4j.core.Logger.class);

        checkNoSuchClass new ClRef("org.slf4j.LoggerFactory");
        checkNoSuchClass new ClRef("org.slf4j.Logger");
        checkNoSuchClass new ClRef("sun.jvmstat.monitor.HostIdentifier");
        checkNoSuchClass new ClRef("com.sun.jna.Native");

    }


}
