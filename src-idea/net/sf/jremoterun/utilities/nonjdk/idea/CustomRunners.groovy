package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
import net.sf.jremoterun.utilities.nonjdk.idea.jrr.JrrIdeaBeanCommon

import javax.swing.*
import java.awt.*
import java.util.List
import java.util.logging.Logger

@CompileStatic
class CustomRunners {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Icon icon = IconLoader.getIcon('/icon/idea/custom_runners.png', CustomRunners);

    static OSIntegrationIdea osInegrationClient;


    static File runnersDir

    static JrrGroovyScriptRunner jrrGroovyScriptRunner = new JrrGroovyScriptRunner();



    static void createCustomRunners3(File runnersDir2 ) {
        createCustomRunners2(OSIntegrationIdea.osIntegrationIdea,runnersDir2)
    }

    static void createCustomRunners2(OSIntegrationIdea osInegrationClient2,File runnersDir2 ) {
        assert runnersDir2.exists()
        runnersDir = runnersDir2
        assert osInegrationClient2!=null
        osInegrationClient = osInegrationClient2
        jrrGroovyScriptRunner.initDigest()
        JPanel panel2 = new JPanel(new FlowLayout())
        JButton refreshButton = new JButton("Refresh")
        refreshButton.addActionListener {
            refresh(panel2)
        }
        refresh(panel2)
        JPanel panel = createCustomRunners();
        panel.add(refreshButton, BorderLayout.NORTH)
        panel.add(panel2, BorderLayout.CENTER)

    }

    static void refresh(JPanel panel2) {
        panel2.removeAll()
        List<File> all = runnersDir.listFiles().toList().findAll { it.isFile() && it.name.endsWith('.groovy') }
        all.each {
            File f = it
            panel2.add(createActionButton(f));
            JButton openFileButton = new JButton("O")
            panel2.add(openFileButton)
            openFileButton.addActionListener{
                osInegrationClient.openFile(f,null);
            }
        }


    }

    static JButton createActionButton(File f) {
        JButton button = new JButton(f.name.replace('.groovy', ''))
        button.addActionListener {
            osInegrationClient.saveAllEditors()
            Runnable r = {
                try {
                    log.info "file ${f} calling .."
                    Class clazz = jrrGroovyScriptRunner.createScriptClass(f.text
                            , f.name)
                    Thread.currentThread().setContextClassLoader(clazz.classLoader)
                    Object instance = clazz.newInstance()
                    JrrClassUtils.invokeJavaMethod(instance, "run")
                    log.info "file ${f} called"
                } catch (Throwable e) {
                    log.info "${f} ${e}"
                    JrrUtilities.showException(f.name, e)
                }
            }
            Thread thread = new Thread(r, "${f.name} custom runner")
            thread.start()
        }
        return button;
    }


    private static JPanel createCustomRunners() {
        JPanel panel = JrrIdeaBeanCommon.bean.customRunners
        if (panel != null) {
            return panel
        }
        Project project = getOpenedProject()
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project)
        panel = new JPanel(new BorderLayout())
        JrrIdeaBeanCommon.bean.customRunnersToolWindow = toolWindowManager.registerToolWindow('Custom runners', panel, ToolWindowAnchor.RIGHT)
        JrrIdeaBeanCommon.bean.customRunners = panel
        JrrIdeaBeanCommon.bean.customRunnersToolWindow.icon = icon
        return panel
    }


    static Project getOpenedProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects == null || openProjects.length == 0) {
            throw new IllegalStateException("Can't find open project");
        }
        return openProjects[0];

    }


}
