package net.sf.jremoterun.utilities.nonjdk.store;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class MapStore {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File file;

    MapStore(File file) {
        this.file = file
    }


    void saveMap(Map<File, String> map){
        file.text = saveMapS(map)
    }

    static String saveMapS(Map<File, String> map){
        map = map.sort()
        String s = map.entrySet().collect {
            String fileName = it.key.absolutePath.replace('\\', '/')
            """b.put( "${fileName}" as File, "${it.value}" );"""
        }.join("\n")

        String s3 = """
// created at ${new SimpleDateFormat('yyyy-MM-dd  HH:mm').format(new Date())} ;
Map<File,String> b = a ;
${s}
"""
        return s3;
    }

    static Map<File, String> loadSettingsS(String scriptSource, String scriptName) {
        Map<File, String> map = new HashMap<>()
        Binding binding = new Binding()
        binding.setVariable('a', map)

        Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName,binding)
        script.run();
        return map
    }

    Map<File, String> loadsettings(File file) {
        try {
            return loadSettingsS(file)
        } catch (Throwable e) {
            log.info("failed load ${file}",e)
            throw e
        }
    }


    static Map<File, String> loadSettingsS(File file) {
        return loadSettingsS(file.text, file.name)
    }

}
