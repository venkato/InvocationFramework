package net.sf.jremoterun.utilities.nonjdk.enumutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class EnumCustomNameResolver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static <T> T resolveEx2(List<T> values, String name) {
        T t = resolve2(values, name)
        if (t == null) {
            throw new IllegalArgumentException("Failed find : ${name}, available : ${values}")
        }
        return t
    }

    static <T> T resolveEx(T[] values, String name) {
        return resolveEx2(values.toList(),name)
    }

    static <T> T resolve(T[] values, String name) {
        return resolve2(values.toList(),name)
    }

    static <T> T resolve2(List<T> values, String name) {
        for (Object val : values) {
            EnumNameProvider res = (EnumNameProvider) val
            if (res.getCustomName() == name) {
                return (T) res
            }
        }
        return null
    }

    static <T> Map<String, T> createMap2(List<T> values) {
        Map<String, T> res2 = [:]
        for (T val : values) {
            EnumNameProvider enum2 = (EnumNameProvider) val
            T before = (T)res2.put(enum2.getCustomName(), val)
            if (before != null) {
                throw new IllegalStateException("Collision for ${val} and ${before}")
            }
        }
        return res2

    }

    static <T> Map<String, T> createMap(T[] values) {
        return createMap2(values.toList())

    }

}
