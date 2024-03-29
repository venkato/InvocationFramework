package net.sf.jremoterun.utilities.nonjdk.downloadutils

import com.pty4j.util.PtyUtil
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
class WinptyDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static URL url = new URL("https://github.com/rprichard/winpty/releases/download/0.4.3/winpty-0.4.3-msvc2015.zip")

    static volatile boolean inited = false

    static void downloadWinpty() {
        if (inited) {

        } else {
            inited = true
            File unzipFiles = init2();
//            copyLinuxNativeLibs(unzipFiles)
        }
    }

    static File init2() {
        File unzip = UrlDownloadUtils3.getUrlDownloadUtils().downloadUrlAndUnzip(url)
        File toDir = new File(unzip, "win/x86_64")
        if (toDir.exists()) {

        } else {
            File f3 = new File(unzip, "x64/bin")
            assert f3.exists()
            assert toDir.mkdirs()
            FileUtilsJrr.copyDirectory(f3, toDir)
        }
        File checkFIle = new File(toDir, 'winpty.dll')
        assert checkFIle.exists()
        log.info "checkFIle : ${checkFIle}"
        setPtyLibFolder(unzip)
        return unzip
    }

    static void setPtyLibFolder(File unzip){
        JrrClassUtils.setFieldValue(PtyUtil, 'PTY_LIB_FOLDER', unzip.getAbsolutePath())
    }

    static void copyLinuxNativeLibs(File toDir){
        File nativeLibs = GitReferences.pty4jLinuxLibs.resolveToFile()
        FileUtilsJrr.copyDirectory(nativeLibs,toDir)
    }



}
