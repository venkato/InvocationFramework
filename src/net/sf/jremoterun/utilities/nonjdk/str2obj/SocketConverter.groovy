package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class SocketConverter implements StringToObjectConverterI2<Socket> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    Socket convert(String str, Type genericArg) {
        List<String> tokenize = str.tokenize(':')
        assert tokenize.size() == 2
        int port = tokenize[1] as int
        return new Socket(tokenize[0], port)
    }
}
