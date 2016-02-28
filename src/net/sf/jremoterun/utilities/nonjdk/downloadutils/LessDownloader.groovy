package net.sf.jremoterun.utilities.nonjdk.downloadutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences

import java.util.logging.Logger

@Deprecated
@CompileStatic
class LessDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static URL url = new URL("http://netix.dl.sourceforge.net/project/gnuwin32/less/394/less-394-bin.zip")

    static volatile File lessCmd

    public static File lessViewer;


    static File getWinLessViewer(){
        return GitReferences.sshConsole.getSpecOnly().resolveToFile().child("lesscycle.bat")
    }


    private static File downloadLess() {
        if (lessCmd==null) {
            lessCmd=init2();
        } else {
        }
        return lessCmd
    }

    private static File init2() {
        File unzip = UrlDownloadUtils3.getUrlDownloadUtils().downloadUrlAndUnzip(url)
        File lessCmd = new File(unzip, "bin/less.exe")
        assert lessCmd.exists()
        return lessCmd
    }


}
