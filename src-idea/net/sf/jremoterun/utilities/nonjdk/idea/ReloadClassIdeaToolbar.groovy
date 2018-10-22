package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.reloadclass.ReloadClassConnectionPanel
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
import net.sf.jremoterun.utilities.nonjdk.idea.jrr.JrrIdeaBeanCommon

import javax.swing.*
import java.awt.*
import java.util.List
import java.util.logging.Logger

/**
 *   <action class="idea.plugins.thirdparty.filecompletion.share.ReloadClassAction" id="idea.plugins.thirdparty.filecompletion.share.ReloadClassAction" text="ReloadClass" description="Reload class" icon="/icons/reload.png">
 <add-to-group group-id="RefactoringMenu" anchor="first"/>
 </action>

 */

@CompileStatic
class ReloadClassIdeaToolbar {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Icon icon = IconLoader.getIcon('/icon/idea/reload_class.png', ReloadClassIdeaToolbar);
    public static String panelNameReloadClass = 'Reload class'
    private ClRef reloadClassMenuRef = new ClRef('idea.plugins.thirdparty.filecompletion.share.ReloadClassAction');




     static void createReloadClassToolbacr(ReloadClassConnectionPanel panel2) {
//        JPanel panel = JrrIdeaBeanCommon.bean.reloadClassToolbar
//        if (panel != null) {
//            return panel
//        }
        Project project = getOpenedProject()
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project)
//        panel = new JPanel(new BorderLayout())
        ToolWindow window = toolWindowManager.registerToolWindow(panelNameReloadClass, panel2.panel, ToolWindowAnchor.RIGHT);
        JrrIdeaBeanCommon.bean.reloadClassToolWindow = window
        JrrIdeaBeanCommon.bean.reloadClassToolbar = panel2
        window.icon = icon
    }


    static Project getOpenedProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects == null || openProjects.length == 0) {
            throw new IllegalStateException("Can't find open project");
        }
        return openProjects[0];

    }


}
