package net.sf.jremoterun.utilities.nonjdk.classpath.refs2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink

import java.util.logging.Logger

@CompileStatic
class CutomJarAdd1 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    public static SfLink idwDockingUrl2 = new SfLink("infonode/InfoNode%20Docking%20Windows/1.6.1/idw-gpl-1.6.1.zip")


    static File downloadIdw() {
        File idwDocking = new UrlDownloadUtils3().downloadUrlAndUnzip(idwDockingUrl2)
        File file = new File(idwDocking, "idw-gpl-1.6.1/lib/idw-gpl.jar")
        return file
    }


}
