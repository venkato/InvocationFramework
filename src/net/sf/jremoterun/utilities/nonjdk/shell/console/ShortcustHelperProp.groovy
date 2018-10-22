package net.sf.jremoterun.utilities.nonjdk.shell.console

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.LoadScriptFromFileUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import org.codehaus.groovy.runtime.MethodClosure;

import java.util.logging.Logger;

@CompileStatic
class ShortcustHelperProp {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private Object value
    private Object value2

    ShortcustHelperProp(Object value) {
        this.value = value
    }


    Object getO() {
        if(value2!=null){
            return value2
        }
        if (value instanceof ClRefRef) {
            value2 = value.getClRef().loadClass2().newInstance()
        } else if (value instanceof Class) {
            value2 = value.newInstance()
        } else if (value instanceof MethodClosure) {
            value2 = value
        }else {
            value2 = value
        }
//        log.info "resolve value2 : ${value2} ${value2.getClass()}"
        return value2
    }

}
