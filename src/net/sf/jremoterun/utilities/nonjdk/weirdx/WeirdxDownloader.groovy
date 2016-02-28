package net.sf.jremoterun.utilities.nonjdk.weirdx;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.MainMethodRunner
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class WeirdxDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static SfLink zipUrl2 = new SfLink("weirdx/WeirdX/1.0.32/weirdx-1.0.32.zip");

    @Deprecated
    public static URL zipUrl = new URL("https://10gbps-io.dl.sourceforge.net/project/weirdx/WeirdX/1.0.32/weirdx-1.0.32.zip");

//    static int inited = false;
    public static ClRef cnr = new ClRef('com.jcraft.weirdx.WeirdX')


    @Deprecated
    static BinaryWithSource download(){
        File unzip = UrlDownloadUtils3.getUrlDownloadUtils().downloadUrlAndUnzip(zipUrl)
        File src = new File(unzip,'weirdx-1.0.32')
        File jar = new File(src,'misc/weirdx.jar')
        assert src.exists()
        assert jar.exists()
        return new BinaryWithSource(jar,src)
    }

    @Deprecated
    static void downloadAndRun(){
        download()
//        File props = new File(source.source,'config/props')
//        assert props.exists()
        run([])
    }

    static void run(List<String> args){
        MainMethodRunner.run(cnr,args)
    }

}
