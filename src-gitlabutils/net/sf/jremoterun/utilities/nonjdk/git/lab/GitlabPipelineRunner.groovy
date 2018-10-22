package net.sf.jremoterun.utilities.nonjdk.git.lab

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.nonjdk.swing.JPanel4FlowLayout
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.PipelineApi
import org.gitlab4j.api.RepositoryApi
import org.gitlab4j.api.models.Branch
import org.gitlab4j.api.models.Pipeline
import org.gitlab4j.api.models.PipelineStatus

import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.MutableComboBoxModel
import javax.swing.SwingUtilities
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

@CompileStatic
abstract class GitlabPipelineRunner {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public enum TextValues {
        Run, View,
    }

    public JPanel4FlowLayout viewAndPanel = new JPanel4FlowLayout();

    public GitLabApi gitLabApi;
    public NameAndTextField project = new NameAndTextField('project', '', 30);
    public JLabel status = new JLabel('')
    public JComboBox<String> branchesSwing = new JComboBox<String>()
    public JButton runOrView = new JButton(TextValues.Run.name())
    public long intervalChecking = 1000
    public Object lock = new Object();
    public Pipeline pipeline
    public Thread thread1;

    GitlabPipelineRunner(GitLabApi gitLabApi,String defaultProject) {
        this.gitLabApi = gitLabApi
        runOrView.addActionListener {
            onClick()
        }
        project.textField.addKeyListener(new KeyAdapter() {


            @Override
            void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    log.info "new project entered ${getProjectPath()}"
                    runOrView.setEnabled(false)
                    refreshBranchesSwingThread()
                }
            }
        })
        doLayout()
        project.setText(defaultProject)
        refreshBranchesSwingThread()
    }

    void refreshBranchesSwingThread(){
        status.setText("Refreshing branches")
        Runnable r = { refreshBranches() }
        thread1 = new Thread(r, 'Gitlab branches refresh jrr')
        thread1.start()

    }

    void doLayout(){
        viewAndPanel.add(project)
        viewAndPanel.add(branchesSwing)
        viewAndPanel.add(runOrView)
        viewAndPanel.add(status)
    }

    void onClick() {
        String text = runOrView.getText()
        if (text == TextValues.Run.name()) {
            runOrView.setEnabled(false)
            Runnable r = { doRun() }
            thread1 = new Thread(r, 'Gitlab pipeline jrr')
            thread1.start()
        }
        if (text == TextValues.View.name()) {
            String webUrl = pipeline.getWebUrl()
            openUrl(webUrl)
        }
    }

    abstract void openUrl(String url);

    void refreshBranches() {
        String projectPath = getProjectPath()
        log.info "refreshing branched for ${projectPath} .."
        try {
            List<String> branchesS = getBranches()
            if(branchesS.size()==0){
                throw new Exception('no branches found')
            }
            SwingUtilities.invokeLater {
                MutableComboBoxModel<String> model = branchesSwing.getModel() as DefaultComboBoxModel<String>
                model.removeAllElements()
                branchesS.each {
                    model.addElement(it)
                }
                branchesSwing.setSelectedIndex(0)
                log.info "refresh done branched for ${projectPath}"
                status.setText("branches fetched")
            }
        } catch (Throwable e) {
            log.info("Failed refresh ${projectPath}", e)
            JrrUtilities.showException("Failed refresh ${projectPath}", e)
            SwingUtilities.invokeLater{
                status.setText("branches fetched failed")
            }
        } finally {
            SwingUtilities.invokeLater {
                runOrView.setEnabled(true)
            }
        }
    }

    List<String> getBranches(){
        RepositoryApi repositoryApi = gitLabApi.getRepositoryApi()
        List<Branch> branchesO = repositoryApi.getBranches(projectPath)
        List<String> branchesS = branchesO.collect { it.getName() }
        if(branchesS.contains('master')){
            branchesS.add(0,'master')
        }
        if(branchesS.contains('main')){
            branchesS.add(0,'main')
        }

        branchesS = branchesS.unique()
        return branchesS
    }

            String getProjectPath() {
        return project.getText().trim()
    }

    void doRun() {
        try {
            doRunImpl()
        } catch (Throwable e) {
            log.info("Failed refresh ${projectPath}", e)
            JrrUtilities.showException("Failed run pipeline ${projectPath}", e)
            SwingUtilities.invokeLater{
                status.setText("failed run pipeline ${e}")
            }
        } finally {
            SwingUtilities.invokeLater {
                runOrView.setText(TextValues.Run.name())
                runOrView.setEnabled(true)
            }
        }
    }

    void doRunImpl() {
        PipelineApi pipelineApi = gitLabApi.getPipelineApi()
        String projectPath = getProjectPath();
        pipeline = pipelineApi.createPipeline(projectPath, (String) branchesSwing.getSelectedItem());
        SwingUtilities.invokeLater {
            runOrView.setText(TextValues.View.name())
            runOrView.setEnabled(true)
        }

        while (true) {
            if (!onRefreshContinue()) {
                break
            }
            synchronized (lock) {
                lock.wait(intervalChecking)
            }
            pipeline = pipelineApi.getPipeline(projectPath, pipeline.getId())
        }
    }

    boolean onRefreshContinue() {
        PipelineStatus pipelineStatus = pipeline.getStatus()
        SwingUtilities.invokeLater {
            status.setText(pipelineStatus.toString())
        }
        log.info("pipeline ${pipeline.getId()} ${pipelineStatus}")
        if (pipeline.getFinishedAt() != null) {
            log.info("pipeline ${pipeline.getId()} finished")
            return false
        }
        return true

    }


}
