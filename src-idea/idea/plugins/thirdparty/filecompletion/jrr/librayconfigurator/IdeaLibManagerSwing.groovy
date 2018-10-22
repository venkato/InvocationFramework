package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.TextFieldWithHistory
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.IdeaJavaRunner2Settings
import idea.plugins.thirdparty.filecompletion.share.JrrIdeaUtils
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.Java4VM
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef
import net.sf.jremoterun.utilities.nonjdk.store.GroovyFileChecker
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanStore
import net.sf.jremoterun.utilities.nonjdk.swing.JPanel4FlowLayout
import org.apache.commons.io.FileUtils

import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdeaLibManagerSwing {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Icon libManagerIcon = IconLoader.getIcon('/icons/lib_manager.png', OSIntegrationIdea);

    JPanel panel = new JPanel(new BorderLayout());

    private final DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("root");

    DefaultMutableTreeNode projectLibNode = new DefaultMutableTreeNode("Project libraries")

    DefaultMutableTreeNode globalLibNode = new DefaultMutableTreeNode("Global libraries")

    public static ToFileRef2 ideaClassPathLatest = BaseDirSetting.baseDirSetting.childL("configs/idea_classpath_files_latest.groovy");
    FileStore listStore = new FileStore(ideaClassPathLatest.resolveToFile());

    JTree tree = new JTree(rootTreeNode)
    JPanel topPanel = new JPanel4FlowLayout()

    JScrollPane scrollPane = new JScrollPane(tree);

    JButton editSettings = new JButton("Edit settings")
    JButton saveSettings = new JButton("Save settings")
    JButton createTemplate = new JButton("Create template")
    TextFieldWithHistory txtDirectoryToSearch = new TextFieldWithHistory();
    JButton fileOpenDialog = new JButton("...")
    JButton openFileInIdea = new JButton("Open file")
    JButton updateUiBorder = new JButton("Update ui border")
    JButton refreshLibsB = new JButton("Refresh libararies")

    LibManager3 libManager3 = new LibManager3(this);

    IdeaLibManagerSwing() {
        File dir = listStore.file.parentFile;
        dir.mkdirs()
        if (!dir.exists()) {
            JrrUtilities.showException("Failed create dir : ${dir}", new FileNotFoundException("Failed create dir : ${dir}"));
        }
//        SettingsRef.config.ideaLibManagerSwing = this
    }

    int getMinWidth() {
        return txtDirectoryToSearch.getMinimumSize().@width
    }

    void setMinWidth(int pixels) {
        log.info "setting min width = ${pixels}"
        JTextField editor = txtDirectoryToSearch.textEditor
        Dimension size = txtDirectoryToSearch.getMinimumSize()
        Dimension size2 = new Dimension(pixels, size.@height)
        txtDirectoryToSearch.setMinimumSize(size2)
        SwingUtilities.invokeLater {
            try {
                txtDirectoryToSearch.updateUI()
            } catch (Throwable e) {
                JrrUtilities.showException("Failed update txtDirectoryToSearch", e);
            }
        }
//        editor.setMinimumSize(size2)
    }


    void openFile() {
        String text = txtDirectoryToSearch.getText()
        log.info "text : ${text}"
        if (text.length() > 0) {
            File file = text as File
            if (file.exists()) {
                OSIntegrationIdea.osIntegrationIdea.openFile(file, "groovy")
            } else {
                JOptionPane.showMessageDialog(txtDirectoryToSearch, "File not found : ${file.canonicalFile.absolutePath}")
            }
        } else {
            JOptionPane.showMessageDialog(txtDirectoryToSearch, "enter file name")
        }
    }

    void refreshLibs() {
        log.info "refershing libs ..."
        globalLibNode.removeAllChildren()
        projectLibNode.removeAllChildren()
        LibraryTablesRegistrar registrar = LibraryTablesRegistrar.getInstance();
        if (true) {
            LibraryTable libraryTable = registrar.getLibraryTable();
            List<Library> libraries = libraryTable.getLibraries().toList();

            List<LibItem> collect = libraries.collect { new LibItem(it) }
            collect = collect.sort()
            collect.collect { new DefaultMutableTreeNode(it) }.each { globalLibNode.add(it) }
        }
        if (true) {
            LibraryTable libraryTable = registrar.getLibraryTable(OSIntegrationIdea.openedProject);
            List<Library> libraries = libraryTable.getLibraries().toList();
            List<LibItem> collect = libraries.collect { new LibItem(it) }
            collect = collect.sort()
            collect.collect { new DefaultMutableTreeNode(it) }.each { projectLibNode.add(it) }
        }
        DefaultTreeModel model = tree.getModel() as DefaultTreeModel
        model.reload()
//        tree.updateUI()
        log.info "lib refreshed"
    }

    void build() {
        panel.add(topPanel, BorderLayout.NORTH)
        topPanel.add(txtDirectoryToSearch)

        topPanel.add(fileOpenDialog)
        topPanel.add(openFileInIdea)
        topPanel.add(updateUiBorder)
        rootTreeNode.add(globalLibNode);
        rootTreeNode.add(projectLibNode);
        topPanel.add(refreshLibsB)
        openFileInIdea.addActionListener {
            try {
                openFile()
            } catch (Throwable e) {
                log.log(Level.INFO, "failed open file", e)
                JrrUtilities.showException("failed open file ", e)
            }
        }
        updateUiBorder.addActionListener {
            try {
                setMinWidth(SettingsRef.config.minWidth);
            } catch (Throwable e) {
                log.info("${e}", e)
                JrrUtilities.showException("failed date ui", e)
            }
        }
        refreshLibsB.addActionListener { refreshLibs() }
        fileOpenDialog.addActionListener(new FileChooserListener(OSIntegrationIdea.openedProject, txtDirectoryToSearch));
        topPanel.add(editSettings)
        editSettings.addActionListener {
            try {
                editSetttingss()
            } catch (Throwable e) {
                JrrUtilities.showException("Failed edit settings", e)
            }
        }
        topPanel.add(saveSettings)
        saveSettings.addActionListener {
            try {
                saveSettings()
            } catch (Throwable e) {
                JrrUtilities.showException("Failed save settings", e)
            }
        }

        topPanel.add(createTemplate)
        createTemplate.addActionListener {
            try {
                createTemplate3()
            } catch (Throwable e) {
                JrrUtilities.showException("Failed save settings", e)
            }
        }


        saveSettings.setEnabled(SettingsRef.locationEdit.resolveToFile().exists())

//        editSettings.setEnabled(!SettingsRef.locationEdit.exists())
        panel.add(scrollPane, BorderLayout.CENTER)
        refreshLibs()
        setHistoryValuesToDropDowns()
        tree.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                log.info("${e}")
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getSelectionPath()
                    def component = path.getLastPathComponent()
                    log.info component.toString()
                    log.info component.class.getName()
                    if (component instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = component as DefaultMutableTreeNode
                        def object = node.getUserObject()
                        log.info object.toString()
                        if (object instanceof LibItem) {
                            LibItem libItem = (LibItem) object;
                            showPopMenu(libItem, e)
                        }
                    }
                }
            }
        })
        tree.expandRow(0)
        tree.setRootVisible(false);
        tree.setShowsRootHandles(false);
        tree.expandRow(0)
        int minSizeCur = SettingsRef.config.minWidth
        if (minSizeCur > 0) {
            log.info "settings min size = ${minSizeCur}"
            setMinWidth(minSizeCur)
        }
    }


    private createTemplate3() {
        File f = txtDirectoryToSearch.text as File
        boolean pass = !f.exists()
        if (!pass) {
            pass = JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "file exist, overwrite ? : ${f}")
        }
        log.info "pass ? ${pass}"
        if (pass) {
            ClassPathCalculatorSup2Groovy calculator = new ClassPathCalculatorSup2Groovy();
            calculator.filesAndMavenIds.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
            List<Class> classes = [JrrUtilities, JrrClassUtils, JrrUtils, Java4VM, JrrClassUtils.currentClass]
            classes.each {
                calculator.addFilesToClassLoaderGroovySave.addFileWhereClassLocated(it)
            }
            f.text = calculator.saveClassPath9()
            openFile()
        }

    }

    private saveSettings() {
        if (SettingsRef.locationEdit.resolveToFile().exists()) {
            Runnable r3 = {
                OSIntegrationIdea.saveAllImpl2();
                if (SettingsRef.locationEdit.resolveToFile().length() < 10) {
                    log.info "file is empty ${SettingsRef.locationEdit}"
                    JOptionPane.showMessageDialog(null, "file is empty ${SettingsRef.locationEdit}")
                } else {
                    Runnable r = {
                        try {
                            saveSettingsImpl()
                        } catch (Throwable e) {
                            log.log(Level.INFO, "failed parse : ${SettingsRef.locationEdit}", e)
                            JrrUtilities.showException("Config is wrong", e);
                        }
                    }
                    Thread t = new Thread(r, 'IdeaLibManagerSaver')
                    t.start()
                }
            }
            JrrIdeaUtils.submitTr(r3)
        } else {
            log.info "file not exists ${SettingsRef.locationEdit}"
            JOptionPane.showMessageDialog(null, "file not exists ${SettingsRef.locationEdit}")
        }
    }

    private saveSettingsImpl() {
        GroovyFileChecker.analize(SettingsRef.locationEdit.resolveToFile().text)
        SettingsRef.loadSettingsS(SettingsRef.locationEdit.resolveToFile())
        FileUtilsJrr.copyFile(SettingsRef.locationEdit.resolveToFile(), SettingsRef.location.resolveToFile())
        SettingsRef.locationEdit.resolveToFile().text = ''
        log.info('settins loaded fine')
        SwingUtilities.invokeLater {
            try {
                setMinWidth(SettingsRef.config.minWidth);
                JOptionPane.showMessageDialog(null, "Setting imported fine")
            } catch (Throwable e) {
                log.info("${e}", e)
                JrrUtilities.showException("failed apply settings", e)
            }
        }


    }

    private void editSetttingss() {
        boolean edit = editSetttingssImpl();
        if (edit) {
            OSIntegrationIdea.osIntegrationIdea.openFile(SettingsRef.locationEdit.resolveToFile(), '1.groovy')
        }
    }

    private boolean editSetttingssImpl() {
        SwingUtilities.invokeLater { saveSettings.setEnabled(true) }
        if (SettingsRef.locationEdit.resolveToFile().exists() && SettingsRef.locationEdit.resolveToFile().length() > 100) {
            return true;
        }
        if (SettingsRef.location.resolveToFile().exists()) {
            FileUtilsJrr.copyFile(SettingsRef.location.resolveToFile(), SettingsRef.locationEdit.resolveToFile())
            return true
        }
        log.info "file not found : ${SettingsRef.location}"
        File parentDir = SettingsRef.location.resolveToFile().getParentFile()
        if (!parentDir.exists()) {
            log.info "creating dirs : ${parentDir}"
            boolean mkdirs = parentDir.mkdirs()
            log.info "dir created : ${mkdirs} : ${parentDir}"
            if (!mkdirs) {
                log.info "failed create dir, existing"
                return false
            }
        }
        SettingsRef.locationEdit.resolveToFile().text = JavaBeanStore.save3(SettingsRef.config)
        log.info "file created"
//        SwingUtilities.invokeLater{editSettings.setEnabled(false)}
        return true;
    }


    private void setHistoryValuesToDropDowns() {
        List<String> libs = []
        libs.addAll listStore.currentList.unique().collect { it.absolutePath.replace('\\', '/') }
        if (IdeaJavaRunner2Settings.libs != null && IdeaJavaRunner2Settings.libs.exists()) {
            libs.add(IdeaJavaRunner2Settings.libs.absolutePath.replace('\\', '/'))
        }
        log.info "dirs from history : ${libs}"

        txtDirectoryToSearch.setHistory(libs);
    }

    void showPopMenu(LibItem libItem, MouseEvent e) {
        log.info("${e}")
        JPopupMenu popupMenu = new JPopupMenu()
        if (true) {
            JMenuItem menuItem = new JMenuItem("Export")
            popupMenu.add(menuItem)
            menuItem.addActionListener { libManager3.runExport(libItem) }
        }
        if (true) {
            JMenuItem menuItem = new JMenuItem("Import")
            popupMenu.add(menuItem)
            menuItem.addActionListener {
                log.info "doing import cp1"
                Runnable r = {
                    log.info "doing import cp2"
                    OSIntegrationIdea.saveAllImpl2();
                    libManager3.runImport(libItem)
                }
                JrrIdeaUtils.submitTr(r)
            }
        }
        if (true) {
            JMenuItem menuItem = new JMenuItem("Add sources")
            popupMenu.add(menuItem)
            menuItem.addActionListener { libManager3.addSources(libItem, null) }
        }
//        popupMenu.setLocation(e.x,e.y);
//        popupMenu.setVisible(true)
        popupMenu.show((Component) e.source, e.x, e.y)
    }


    static JPanel createIdeaPanel1() {
        JPanel panel = JrrIdeaLinManager.bean.libManagerPanel
        if (panel != null) {
            return panel
        }
        Project project = OSIntegrationIdea.openedProject
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project)
        panel = new JPanel(new BorderLayout())
        JrrIdeaLinManager.bean.customRunnersToolWindow = toolWindowManager.registerToolWindow('Library manager', panel, ToolWindowAnchor.RIGHT)
        JrrIdeaLinManager.bean.libManagerPanel = panel
        JrrIdeaLinManager.bean.customRunnersToolWindow.icon = libManagerIcon
        return panel
    }


    static void createIdeaPanel12() {
        JPanel panel = createIdeaPanel1();
        refreshIdeaPanel(panel)

    }

    static void refreshIdeaPanel(JPanel panel2) {
        panel2.removeAll()
        IdeaLibManagerSwing ideaLibManagerSwing = new IdeaLibManagerSwing()
        ideaLibManagerSwing.build()
        panel2.add(ideaLibManagerSwing.panel, BorderLayout.CENTER)


    }

}
