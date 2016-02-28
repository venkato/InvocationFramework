package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.wm.ToolWindow;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.jrrbean.JrrBeanMaker

import javax.swing.JPanel;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrIdeaLinManager {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static JrrIdeaLinManager bean = JrrBeanMaker
            .makeBeanAndRegisterMBeanNoEx(JrrIdeaLinManager);

    JPanel libManagerPanel;


    ToolWindow customRunnersToolWindow


}
