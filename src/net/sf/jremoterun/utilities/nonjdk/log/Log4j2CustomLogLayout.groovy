package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.logging.log4j.core.LogEvent

import java.util.logging.Logger

@CompileStatic
class Log4j2CustomLogLayout extends Log4j2PatternLayout{

    public List<String> additionalIgnore = []

    @Override
    boolean acceptStackTraceElement(StackTraceElement stackTraceElement) {
        boolean accept = super.acceptStackTraceElement(stackTraceElement)
        if(accept){
            String lcassName = stackTraceElement.getClassName();
            for (String ignore : additionalIgnore) {
                boolean res = lcassName.startsWith(ignore);
                if (res) {
                    return false;
                } else {

                }
            }
        }
        return accept
    }

    @Override
    String toSerializableImpl(LogEvent event) {
        return super.toSerializableImpl(event)
    }
}
