package net.sf.jremoterun.utilities.nonjdk.store;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilePhase;

import java.util.logging.Logger;

import groovy.transform.CompileStatic;

@CompileStatic
public class ClassChecker2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClassChecker2 classChecker2 = new ClassChecker2();

    public boolean passOnError = false

    void analize2(String groovyText) {
        try {
            GroovyFileChecker.analize2(groovyText)
        }catch (Throwable e){
            log.info "failed parse :\n${groovyText}"
            if(!passOnError) {
                throw e;
            }
        }
    }

}
