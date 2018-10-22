package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.util.lang.UrlClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

import java.nio.file.Path
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
        boolean added = false;
        if(classLoader3 instanceof UrlClassLoader){
            if(IdeaClasspathAdd.addFileMethod!=null){
                File f = UrlToFileConverter.c.convert(classLocation);
                List<Path> paths = [f.toPath()];
                IdeaClasspathAdd.addFileMethod.invoke(classLoader3,paths);
                added = true;
            }
        }
        if(!added) {
            JrrClassUtils.invokeJavaMethod(classLoader3, "addURL", classLocation);
        }
        log.info("AddJrrLibToCommonIdeaClassloader done");
    }

}
