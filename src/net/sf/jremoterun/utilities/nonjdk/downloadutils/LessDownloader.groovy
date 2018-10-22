package net.sf.jremoterun.utilities.nonjdk.downloadutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs

import java.util.logging.Logger


@CompileStatic
class LessDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // https://sourceforge.net/projects/gnuwin32/files/less/394/  -  doesn't work
    private static URL url = new URL("https://steve.fi/Software/less/less-332.zip")

    public static FileChildLazyRef lessCycle = GitSomeRefs.sshConsole.childL("lesscycle.bat")


    static volatile File lessCmd

    public static File lessViewer;


    static FileChildLazyRef getWinLessViewer(){
        return lessCycle
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
        File lessCmd = new File(unzip, "less-332/Binaries/less.exe")
        assert lessCmd.exists()
        return lessCmd
    }


}
