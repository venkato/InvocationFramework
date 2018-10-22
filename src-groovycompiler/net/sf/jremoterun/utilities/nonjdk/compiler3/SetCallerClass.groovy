package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.callerclass.GetCallerClassI
import net.sf.jremoterun.callerclass.GetCallerClassS
import net.sf.jremoterun.utilities.classpath.ClRef

@CompileStatic
class SetCallerClass implements Map{

//    public static ClRef clRefJava8 = new ClRef("net.sf.jremoterun.callerclass.java8.GetCallerClass")
//    public static ClRef clRefJava9 = new ClRef("net.sf.jremoterun.callerclass.java9.GetCallerClass")

    @Override
    Object get(Object key) {
        doStuff(key as boolean)
        return null
    }

    void doStuff(boolean  isJava11){
        String clRef1 = isJava11? GetCallerClassS.java9ClassImpl:GetCallerClassS.java8ClassImpl
        GetCallerClassI instance1 = SetCallerClass.getClassLoader().loadClass(clRef1).newInstance() as GetCallerClassI
        GetCallerClassS.getCallerClassI = instance1;
    }

    @Override
    int size() {
        return 0
    }

    @Override
    boolean isEmpty() {
        return false
    }

    @Override
    boolean containsKey(Object key) {
        return false
    }

    @Override
    boolean containsValue(Object value) {
        return false
    }

    @Override
    Object put(Object key, Object value) {
        return null
    }

    @Override
    Object remove(Object key) {
        return null
    }

    @Override
    void putAll(Map m) {

    }

    @Override
    void clear() {

    }

    @Override
    Set keySet() {
        return null
    }

    @Override
    Collection values() {
        return null
    }

    @Override
    Set<Entry> entrySet() {
        return null
    }
}
