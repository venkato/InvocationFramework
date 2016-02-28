package net.sf.jremoterun.utilities.nonjdk.str2obj;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.Type;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class InetAddressConverter implements StringToObjectConverterI2<InetAddress>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    InetAddress convert(String str, Type genericArg) {
        return InetAddress.getByName(str)
    }
}
