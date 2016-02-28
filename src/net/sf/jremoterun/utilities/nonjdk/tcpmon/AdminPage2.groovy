package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic
import net.infonode.docking.TabWindow
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunner

import javax.swing.*
import java.util.logging.Logger

/**
 * this is the admin page
 */
@CompileStatic
public class AdminPage2 extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public View defaultView;

    public AdminPage2(File file) {
        super(file);
        runInSwingThread = true
    }

    void onNewListener(Listener listener) {

    }

    @Override
    public void runCode() {
//        getParams().put("tabWindow", (TabWindow) defaultView
//                .getWindowParent());
        super.runCode();
    }


}
