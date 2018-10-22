package net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import java.util.logging.Logger

@CompileStatic
class CopyResourcesFromDirToDir {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void copyResourcesFromDirToDir(File inDir, File outDir) {
        List<File> childs = inDir.listFiles().toList()
        childs.each {
            File destFile2 = new File(outDir, it.name)
            if (it.isDirectory()) {
                destFile2.mkdir()
                copyResourcesFromDirToDir(it, destFile2)
            } else {
                if(destFile2.exists()){
                    log.info "dest file exist ${destFile2}, original file : ${it}"
                }  else {
                    String extension = FilenameUtils.getExtension(it.name)
                    if (extension != null && (extension == 'groovy' || extension == 'java')) {

                    } else {
                        FileUtilsJrr.copyFile(it, destFile2)
                    }
                }
            }
        }
    }

}
