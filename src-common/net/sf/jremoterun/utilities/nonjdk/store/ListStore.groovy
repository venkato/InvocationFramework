package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
class ListStore<T> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String varName = "a"

    File file;

    ListStore(File file) {
        this.file = file
    }


    void saveToFile(List<T> list) {
        file.text = saveS(list)
    }

    String saveS(List<T> list) {
        Writer3 writer3 = new Writer3Sub()
        ObjectWriter writer = new ObjectWriter()
        writer3.addCreatedAtHeader()
        writer3.body.add "List b = ${writer3.generateGetProperty(varName)} as List;".toString()
        writer3.body.addAll list.collect {
            String obj = writer.writeObject(writer3, it)
            "b.add ${obj} ;" as String
        }
        return writer3.buildResult();
    }

    List<T> loadSettingsS(String scriptSource, String scriptName) {
        List<T> list = []
        Binding binding = new Binding()
        binding.setVariable(varName, list)

        Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName, binding)
        script.run();
        return list
    }

    List<T> loadsettings() {
        if (file.exists()) {
            try {
                return loadSettingsS(file.text, file.name)
            } catch (Throwable e) {
                log.info("failed load : ${file}", e)
                throw e
            }
        }
        return []
    }

//    List<T> loadSettingsS(File file) {
//        return loadSettingsS(file.text, file.name)
//    }

}
