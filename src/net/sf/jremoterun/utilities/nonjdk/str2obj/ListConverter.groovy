package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class ListConverter implements StringToObjectConverterI {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String separator = ','

    @Override
    Object convert(String str, Type genericArg) {
        convert2(str, genericArg)
    }

    static List convert2(String str, Type genericArg) {
        Class clazz = getParamClass(genericArg)
        if (clazz == null) {
            throw new IllegalArgumentException("failed find converter from collection ${genericArg} for ${str}")
        }
        List<String> tokenize = str.tokenize(separator)
        return tokenize.collect {
            Object el = StringToObjectConverter.defaultConverter.convertFromStringToType(it, clazz, null)
            return el
        }
    }

    static Class getParamClass(Type genericArg) {
        if (!(genericArg instanceof ParameterizedType)) {
            throw new IllegalArgumentException("not ParameterizedType : ${genericArg}")
//            return null
        }
        Type[] typeArguments = genericArg.getActualTypeArguments()
        if (typeArguments.length != 1) {
            throw new IllegalArgumentException("type should be 1 : ${typeArguments}")
        }
        Type type = typeArguments[0]
        if (!(type instanceof Class)) {
            throw new IllegalArgumentException("not a class : ${type}")
        }
        Class clazz = (Class) type;
        if (clazz == Object) {
            clazz = String
        }
        return clazz
    }

}
