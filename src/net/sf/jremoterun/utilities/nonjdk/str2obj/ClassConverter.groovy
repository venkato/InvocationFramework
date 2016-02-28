package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class ClassConverter implements StringToObjectConverterI2<Class>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassConverter() {
    }

    @Override
    Class convert(String str, Type genericArg) {
        return Class.forName(str)
    }
}
