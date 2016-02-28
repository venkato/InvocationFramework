package net.sf.jremoterun.utilities.nonjdk.langimprovetesters;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AllTesters implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        new ExtMethodsTester().run()
        new FieldAccessTester().run()
    }
}
