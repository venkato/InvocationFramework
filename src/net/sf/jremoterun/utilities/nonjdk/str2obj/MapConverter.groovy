package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class MapConverter implements StringToObjectConverterI {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String separator = ','
    public static String separatorKeyValue = ':'

    @Override
    Object convert(String str, Type genericArg) {
        convert2(str, genericArg)
    }

    static Map convert2(String str, Type genericArg) {
        List<Class> clazz1 = getParamClass(genericArg)
        if (clazz1 == null) {
            throw new IllegalArgumentException("failed find converter from collection ${genericArg} for ${str}")
        }
        List<String> tokenize = str.tokenize(separator)
        Map result = [:]
        tokenize.each {
            List<String> keyValue = it.tokenize(separatorKeyValue)
            if (keyValue.size() != 2) {
                throw new IllegalArgumentException("stange key-value ${it}")
            }
            Object key = StringToObjectConverter.defaultConverter.convertFromStringToType(keyValue[0], clazz1[0], null)
            Object value = StringToObjectConverter.defaultConverter.convertFromStringToType(keyValue[1], clazz1[1], null)
            result.put(key, value)
        }
        return result
    }

    static List<Class> getParamClass(Type genericArg) {
        if (!(genericArg instanceof ParameterizedType)) {
            throw new IllegalArgumentException("not ParameterizedType : ${genericArg}")
        }
        ParameterizedType pt = (ParameterizedType) genericArg;
        Type[] typeArguments = pt.getActualTypeArguments()
        if (typeArguments.length != 2) {
            throw new IllegalArgumentException("need 2 args : ${typeArguments}")
        }
        Type typeKey = typeArguments[0]
        if (!(typeKey instanceof Class)) {
            throw new IllegalArgumentException("not a class : ${typeKey}")
        }
        Class typeKey2 = typeKey
        if (typeKey2 == Object) {
            typeKey2 = String
        }
        Type typeValue = typeArguments[1]
        if (!(typeValue instanceof Class)) {
            throw new IllegalArgumentException("not a class : ${typeValue}")
        }
        Class typeValue2 = typeValue
        if (typeValue2 == Object) {
            typeValue2 = String
        }
        return [typeKey2, typeValue2]
    }

}
