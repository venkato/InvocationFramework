package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class File2HashMapJsonSaver {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void saveToJson(Map<File, String> fileCache3, File f) {
        Map<String, String> fileCache5 = (Map) fileCache3.collectEntries {
            [(it.key.canonicalFile.absolutePath.replace('\\', '/')): it.value]
        }
        String json = JsonOutput.toJson(fileCache5)
        json = JsonOutput.prettyPrint(json)
        f.text = json
    }


    static Map<File, String> readJson2(File f) {
        if(f.exists()){
            return readJson(f)
        }
        return [:]
    }

    static Map<File, String> readJson(File f) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map<String, String> map3 = (Map) jsonSlurper.parse(f)
        Map<File, String> entries = (Map) map3.collectEntries { [(new File(it.key)): it.value] }
        return entries
    }


}
