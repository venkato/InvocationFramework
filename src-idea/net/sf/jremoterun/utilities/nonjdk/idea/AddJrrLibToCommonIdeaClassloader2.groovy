package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

/**
 * Created by nick on 08.03.2017.
 */
@CompileStatic
public class AddJrrLibToCommonIdeaClassloader2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean libAdded = false;

    public static void addJrrLibToCommonIdeaClassloader3() throws Exception {
        if (libAdded) {

        } else {
            libAdded = true;
            addJrrLibToCommonIdeaClassloaderImpl();
        }
    }

    public static void addJrrLibToCommonIdeaClassloaderImpl() throws Exception {
        log.info("try to AddJrrLibToCommonIdeaClassloader ... ");
        ClassLoader classLoader3 = PluginClassLoader.getClassLoader();
        log.info "classLoader3 : ${classLoader3}"
        assert classLoader3 != null

        URL classLocation = JrrUtils.getClassLocation(JrrUtils);
        log.info "JrrUtils location : ${classLocation}"
        JrrClassUtils.invokeJavaMethod(classLoader3, "addURL", classLocation);
        log.info("AddJrrLibToCommonIdeaClassloader done");
    }

}
