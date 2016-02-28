package net.sf.jremoterun.utilities.nonjdk.store;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
class ListSettingsLoader {
    private static final Logger log = Logger.getLogger(JrrClassUtils.currentClass.name);



    List loadsettings(String scriptSource, String scriptName) {
        return loadSettingsS(scriptSource,scriptName)
    }

    static List loadSettingsS(String scriptSource, String scriptName) {
        List list23 = []
        Binding binding = new Binding()
        binding.setVariable('a', list23)

        Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName,binding)
        script.run();
        return list23
    }

    List loadsettings(File file) {
        return loadSettingsS(file)
    }

    static List loadSettingsS(File file) {
        try{
        return loadSettingsS(file.text, file.name)
        } catch (Throwable e) {
            log.info("failed load ${file}",e)
            throw e
        }
    }

//    static String saveSettingsS(List list) {
//        String s = list.collect {
//            String fileName = it.absolutePath.replace('\\', '/')
//            """b.put( "${fileName}" as File, "${it.value}" );"""
//        }.join("\n")
//
//        String s3 = """
//// created at ${new Date().format('yyyy-MM-dd  HH:mm')}
//Map<File,String> b = a ;
//${s}
//"""
//        return s3;
//
//    }
}
