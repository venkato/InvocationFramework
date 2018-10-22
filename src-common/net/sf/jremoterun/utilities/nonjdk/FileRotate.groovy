package net.sf.jremoterun.utilities.nonjdk;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.io.FileUtils

import java.text.DecimalFormat
import java.util.logging.Logger

@CompileStatic
class FileRotate {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean copyIfFailedRenameG = true;
    public static boolean doSync = true;

    static void rotateFile(File file, File archiveRotationDir, int maxDepth) {
        if (file.exists()) {
            file = file.getAbsoluteFile().getCanonicalFile()
            rotateFileImplSync(file, archiveRotationDir, 0, maxDepth, true, copyIfFailedRenameG)
        }
    }

    static void rotateFile(File file, int maxDepth) {
        if (file.exists()) {
            if (file.isFile() && file.length() == 0) {

            } else {
                file = file.getAbsoluteFile().getCanonicalFile()
                rotateFileImpl(file, 0, maxDepth)
            }
        }
    }

    static void rotateFileImpl(File file, int depth, int maxDepth) {
        rotateFileImplSync(file, file.parentFile, depth, maxDepth, true, copyIfFailedRenameG);
    }


    static void rotateFileImplSync(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename) {
        if (doSync) {
            String syncOnObject = file.getAbsoluteFile().getCanonicalFile().getAbsolutePath().intern()
            synchronized (syncOnObject) {
                rotateFileImpl(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename)
            }
        } else {
            rotateFileImpl(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename)
        }
    }

    static void rotateFileImpl(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename) {
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
                rotateFileImpl(file, archiveRotationDir, newDepth, maxDepth, true, copyIfFailedRename);
                assert !file2.exists()
            }
        }
        assert !file2.exists()
        if (doRenameOrCopy) {
            boolean renameDone = fileFrom.renameTo(file2)
            if (!renameDone) {
                if (copyIfFailedRename) {
                    log.info "Failed rename ${fileFrom} to ${file2}, do coping .."
                    if(fileFrom.isDirectory()){
                        FileUtilsJrr.copyDirectory(fileFrom, file2);
                        FileUtils.deleteQuietly(fileFrom )
                    }else {
                        FileUtilsJrr.copyFile(fileFrom, file2);
                        fileFrom.delete()
                    }
//                    if(fileFrom.exists()){
//                        throw new Exception("Failed delete file : ${fileFrom}")
//                    }
                } else {
                    throw new IOException("Failed rename ${fileFrom} to ${file2}")
                }
            }
        } else {
            FileUtilsJrr.copyFile(file, file2)
        }
    }


}
