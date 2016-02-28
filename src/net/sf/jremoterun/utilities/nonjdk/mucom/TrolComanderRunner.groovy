package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils

//import com.mucommander.commons.file.util.ResourceLoader
//import com.mucommander.ui.icon.IconManager;
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.MainMethodRunner

import java.util.logging.Logger

@CompileStatic
class TrolComanderRunner implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static   ClRef cnr = new ClRef('com.mucommander.TrolCommander')

    @Override
    void run() {
        MuCommanderLoggerCreator.createAndDefine();
//        ClassLoader dc = JrrClassUtils.getFieldValue(ResourceLoader,'defaultClassLoader') as ClassLoader
//        log.info "${dc}"
//
//        String folder = IconManager.getIconSetFolder(IconManager.MUCOMMANDER_ICON_SET);
//        log.info "${folder}"
//        URL l = ResourceLoader.getResourceAsURL(folder + "splash.png")
//
//        log.info "${l}"
//        assert l!=null
        log.info "${UrlCLassLoaderUtils.getClassLocation(org.slf4j.helpers.Util)}"
        List<String> args2 = []
        MainMethodRunner.run(cnr,args2)
//        Class clazz = this.class.classLoader.loadClass(cnr.className)
//
//        JrrClassUtils.runMainMethod( clazz,args2)
    }
}
