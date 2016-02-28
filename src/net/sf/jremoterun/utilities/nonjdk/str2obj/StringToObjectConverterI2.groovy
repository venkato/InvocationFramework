package net.sf.jremoterun.utilities.nonjdk.str2obj

import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.Type


interface StringToObjectConverterI2<T> extends StringToObjectConverterI{

    @Override
    T convert(String str, Type genericArg)
}