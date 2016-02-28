package net.sf.jremoterun.utilities.nonjdk;

import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils

import java.text.DecimalFormat

@CompileStatic
class FileRotate {

    static void rotateFile(File file, File archiveRotationDir, int maxDepth) {
        if (file.exists()) {
            file = file.absoluteFile.canonicalFile
            rotateFileImpl(file, archiveRotationDir, 0, maxDepth,true)
        }
    }

    static void rotateFile(File file, int maxDepth) {
        if (file.exists()) {
            file = file.absoluteFile.canonicalFile
            rotateFileImpl(file, 0, maxDepth)
        }
    }

    static void rotateFileImpl(File file, int depth, int maxDepth) {
        rotateFileImpl(file, file.parentFile, depth, maxDepth,true);
    }

    static void rotateFileImpl(File file, File archiveRotationDir, int depth, int maxDepth,boolean doRenameOrCopy) {
        int newDepth = depth + 1
        DecimalFormat df = new DecimalFormat("00");
        String dirPrefix = "${file.name}.${df.format(depth)}"
        File fileFrom = depth == 0 ? file : new File(archiveRotationDir, dirPrefix)
        String newDepthString = "${file.name}.${df.format(newDepth)}"
        File file2 = new File(archiveRotationDir, newDepthString)
        if (file2.exists()) {
            if (newDepth > maxDepth) {
                if (file2.isDirectory()) {
                    FileUtils.deleteDirectory(file2)
                } else {
                    assert file2.delete()
                }
                assert !file2.exists()
            } else {
                rotateFileImpl(file, archiveRotationDir, newDepth, maxDepth,true);
            }
        }
        assert !file2.exists()
        if(doRenameOrCopy) {
            assert fileFrom.renameTo(file2)
        }else{
            FileUtils.copyFile(file,file2)
        }
    }


}
