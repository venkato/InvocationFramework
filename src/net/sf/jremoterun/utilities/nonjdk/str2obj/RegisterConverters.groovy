package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class RegisterConverters implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        regConverters2()
    }

    static void regConverters2() {
        Map<Class, StringToObjectConverterI> converters = StringToObjectConverter.defaultConverter.customConverters
        regConverters(converters)
    }

    static void regConverters(Map<Class, StringToObjectConverterI> cust) {
        ListConverter listConverter = new ListConverter()
        cust.put(List, listConverter);
        cust.put(Collection, listConverter);
        cust.put(Set, new SetConverter());
        cust.put(Map, new MapConverter());
        regConverters3(cust, new SocketConverter())
        regConverters3(cust, new ClassConverter())
        regConverters3(cust, new InetAddressConverter())
        regConverters3(cust, new DateOnlyBackConverter())
//        log.info "converter registered"
    }


    static void regConverters3(Map<Class, StringToObjectConverterI> cust, StringToObjectConverterI2 converter) {
        Class<?> clazz = converter.getClass()
        Class aClass = deriveType(clazz)
//        log.info "${aClass.name} ${clazz.name}"
        cust.put(aClass, converter)
    }


    static Class deriveType(Class clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        List<ParameterizedType> paramType = (List)interfaces.toList().findAll { it instanceof ParameterizedType }
        ParameterizedType parameterizedType = paramType.find { it.rawType == StringToObjectConverterI2 }
        Class aClass = parameterizedType.actualTypeArguments[0]
        if (aClass == null) {
            throw new IllegalArgumentException("${clazz}")
        }
        return aClass
    }


}
