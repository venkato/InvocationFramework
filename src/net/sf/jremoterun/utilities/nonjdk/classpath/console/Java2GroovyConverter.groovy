package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import org.codehaus.groovy.antlr.java.Java2GroovyProcessor

import java.util.logging.Logger

/**
 * !! This tool delete comments!!
 */
@CompileStatic
class Java2GroovyConverter implements ClassNameSynonym {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void convertJustDisplay(File file) {
        String result = Java2GroovyProcessor.convert(file.absolutePath, file.text, false, true)
        log.info "result : \n${result}"
    }

    static void convertAndWrite(File file, boolean deleteOriginal) {
        String result = Java2GroovyProcessor.convert(file.absolutePath, file.text, false, true)
        File parentFile = file.parentFile
        String fileName = file.name.replace('.java', '.groovy')
        File newName = parentFile.child(fileName)
        newName.text = result
        if (deleteOriginal) {
            assert file.delete()
        }
    }


}
