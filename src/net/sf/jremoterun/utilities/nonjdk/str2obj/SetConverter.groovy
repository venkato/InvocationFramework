package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class SetConverter implements StringToObjectConverterI{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object convert(String str, Type genericArg) {
        return new HashSet(ListConverter.convert2(str,genericArg))
    }
}
