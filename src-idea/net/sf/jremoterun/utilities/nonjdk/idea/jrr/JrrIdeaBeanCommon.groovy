package net.sf.jremoterun.utilities.nonjdk.idea.jrr

import com.intellij.openapi.wm.ToolWindow
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.reloadclass.ReloadClassConnectionPanel;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.jrrbean.JrrBeanMaker

import javax.swing.JPanel;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrIdeaBeanCommon {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static JrrIdeaBeanCommon bean = JrrBeanMaker
            .makeBeanAndRegisterMBeanNoEx(JrrIdeaBeanCommon);

    JPanel customRunners;
    ReloadClassConnectionPanel reloadClassToolbar;


    ToolWindow reloadClassToolWindow
    ToolWindow customRunnersToolWindow
}
