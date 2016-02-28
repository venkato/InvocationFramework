package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.ide.plugins.cl.PluginClassLoader

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import org.codehaus.groovy.runtime.callsite.CallSiteArray
import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite

import java.lang.reflect.Method
import java.util.logging.Logger

@CompileStatic
public class IdeaRedefineClassloader implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String ideaPluginId

    public static void redifineClassloader3() throws Exception {
        PluginClassLoader pluginClassLoader = JrrClassUtils.currentClassLoader as PluginClassLoader
        String pluginId = pluginClassLoader.pluginId.toString()
        log.info "detected plugin pluginId = ${pluginId}"
        redifineClassloader2(pluginId)
    }

    public static void redifineClassloader2(String pluginId) throws Exception {
        ideaPluginId = pluginId
        redifineClassloader()
    }

    public static void redifineClassloader() throws Exception {
        if(ideaPluginId==null){
            throw new Exception("ideaPluginId not defined")
        }
        log.info("PluginClassLoader try to redefine ... ");
        log.info("groovy1 file ${UrlCLassLoaderUtils.getClassLocation(CallSiteArray)}");
        log.info("groovy2 file ${UrlCLassLoaderUtils.getClassLocation(MetaClassConstructorSite)}");
        JrrJavassistUtils.init();
        Class ccc = PluginClassLoader;
        log.info("PluginClassLoader class location : " + JrrUtils.getClassLocation(ccc));
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(ccc);
        final CtMethod method = JrrJavassistUtils.findMethod(ccc, cc, "loadClass", 2);
        Method method1
        String values
        try {
            method1 = JrrClassUtils.findMethodByCount(ccc,'loadClassFromParents', 2)
            values = """ loadClassFromParents(\$1, null) """
        }catch (NoSuchMethodException e){
            log.info "${e}"
            Set anySetValue = new HashSet()
            method1 = JrrClassUtils.findMethodByParamTypes1(ccc,'a','anyvalue',anySetValue)
            values = """ a(\$1,null) """

//            method1 = JrrClassUtils.findMethodByParamTypes1(ccc,'b','anyvalue')
//            values = """ b(\$1) """
         }

        assert method1.getReturnType() == Class

        method.insertBefore """            
            if("${ideaPluginId}".equals(getPluginId().toString() ) ) {
                Class classLoadedByParent4 = ${values};
                if(classLoadedByParent4 != null){ 
                    return classLoadedByParent4; 
                };
            }
""";

        log.info(method.toString());
        JrrJavassistUtils.redefineClass(cc, ccc);
        log.info("PluginClassLoader redefine done");
    }

    @Override
    void run() {
        redifineClassloader3();
    }
}