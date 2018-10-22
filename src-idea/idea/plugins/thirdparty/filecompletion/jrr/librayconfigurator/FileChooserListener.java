package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistory;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class FileChooserListener implements ActionListener {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private final FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
    private Project project;
    private TextFieldWithHistory dirHistory;

    public FileChooserListener(Project project, TextFieldWithHistory dirHistory) {
        this.project = project;
        this.dirHistory = dirHistory;
    }

    public void actionPerformed(ActionEvent actionEvent) {

        VirtualFile baseDir = getBaseDir();

        VirtualFile[] files;


        files = FileChooser.chooseFiles(descriptor, project, baseDir);
        if ((files != null) && (files.length > 0)) {
            dirHistory.setSelectedItem(files[0].getPresentableUrl());
        }
    }

    private VirtualFile getBaseDir() {
        VirtualFile baseDir = null;
        try {
            if ((dirHistory.getText() != null) && dirHistory.getText().length() > 0) {
                File file = new File(dirHistory.getText());
                if (file.exists()) {
                    baseDir = VfsUtil.findFileByURL(file.toURL());
                }
            }
        } catch (MalformedURLException e) {
  			log.log(Level.WARNING,"",e);
        }
        return baseDir;
    }
}
