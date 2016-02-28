package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class DateConverter implements StringToObjectConverterI2<Date>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    SimpleDateFormat sdf

    DateConverter(SimpleDateFormat sdf) {
        this.sdf = sdf
    }

    @Override
    Date convert(String str, Type genericArg) {
        return sdf.parse(str)
    }
}
