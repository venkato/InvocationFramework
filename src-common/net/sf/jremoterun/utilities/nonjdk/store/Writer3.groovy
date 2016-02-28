package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilePhase

import java.util.logging.Logger

@CompileStatic
class Writer3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<String> header = [];

    List<String> importss = []

    List<String> body = [];

    Writer3() {
    }

    void addCreatedAtHeader(){
        header.add  "// created at ${new Date().format('yyyy-MM-dd  HH:mm')}" as String
    }

    String buildResult() {
        List<String> res = header + importss.collect { "import ${it} ;" as String} + [''] + body+['']
        String res3 =  res.join('\n');
        GroovyFileChecker.analize(res3,CompilePhase.CANONICALIZATION, true ,false)
        return res3;
    }

    void addImport(Class clazz) {
        if (importss.contains(clazz.name)) {

        } else {
            importss.add(clazz.name)
        }
    }

    String generateGetProperty(String propName){
        return " binding.getProperty('${propName}') "
    }

}
