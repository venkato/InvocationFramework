package net.sf.jremoterun.utilities.nonjdk.groovy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass
import org.codehaus.groovy.runtime.m12n.ExtensionModule
import org.codehaus.groovy.runtime.m12n.ExtensionModuleRegistry
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;

import java.util.logging.Logger;

@CompileStatic
class ExtentionMethodChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void check() {
        MetaClassRegistryImpl metaClassRegistry = GroovySystem.getMetaClassRegistry() as MetaClassRegistryImpl;
        ExtensionModuleRegistry moduleRegistry = metaClassRegistry.getModuleRegistry();
        MetaInfExtensionModule module = moduleRegistry.getModule('JdkLoggingMethods') as MetaInfExtensionModule
        if(module==null){
            throw new Exception('JdkLoggingMethods module not found')
        }
        List<Class> classes = module.getInstanceMethodsExtensionClasses()
        if(classes.size()!=2){
            throw new Exception("wrong classes count = ${classes.size()}")
        }
        if(!classes.contains(JdkLoggerExtentionClass)){
            throw new Exception("${JdkLoggerExtentionClass.getName()} not exist in ${classes}")
        }
        if(!classes.contains(FileExtentionClass)){
            throw new Exception("${FileExtentionClass.getName()} not exist in ${classes}")
        }
    }
}
