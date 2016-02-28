package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.io.FilenameUtils

import java.util.logging.Logger

@CompileStatic
class ReplaceTextInDir {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<String> textExts = ['properties', 'txt', 'sh', 'bat', 'props',]
    List<String> chmodExts = ['sh',]

    void replaceStr(File dir, String from, String to) {
        if (dir.isFile()) {
            if (isReplaceInFile(dir)) {
                replaceText(dir, from, to)
            }
        } else {
            assert dir.directory
            File[] files = dir.listFiles()
            for (File f : files) {
                replaceStr(f, from, to)

            }
        }
    }

    void chmodD(File dir) {
        if (dir.file) {
            String extension = FilenameUtils.getExtension(dir.name)
            if (chmodExts.contains(extension)) {
                chmodF(dir)
            }
        } else {
            assert dir.directory
            File[] files = dir.listFiles()
            for (File f : files) {
                chmodD(f)
            }
        }
    }


    void chmodF(File f) {
        assert f.exists()
        assert f.canWrite()
        f.setExecutable(true, true)
    }

    void replaceText(File f, String from, String to) {
        f.text = f.text.replace(from, to)
    }

    boolean isReplaceInFile(File f) {
        String name = f.name
        String extension = FilenameUtils.getExtension(name)
        return textExts.contains(extension) && f.length() < 3_000_000
    }

}
