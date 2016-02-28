package net.sf.jremoterun.utilities.nonjdk.idea.init;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class MyInitWrapper2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean inited = false;

    public static Exception initCallStack;

    public static void init() {
        try {
            if (inited) {
                //log.log(Level.SEVERE,"already inited");
                log.info("already inited");
                return;
            }
            inited = true;
            initCallStack = new Exception("init call stack");
            log.info("loading jars 2");
            if (!IdeaClasspathAdd.inited) {
                IdeaClasspathAdd.init();
            }
//            InitMyWrapper.doMyInit();
            log.info("my initilize done 2");
        } catch (Throwable e) {
            log.log(Level.SEVERE, "failed init idea", e);
            JrrUtilities.showException("Failed init idea", e);

        }
    }

    public static boolean init2() {
        init();
        return inited;
    }
}
