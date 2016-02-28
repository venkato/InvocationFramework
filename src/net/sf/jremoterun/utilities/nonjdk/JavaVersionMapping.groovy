package net.sf.jremoterun.utilities.nonjdk

import javassist.bytecode.ClassFile;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.collections4.MapUtils

import java.lang.reflect.Field;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JavaVersionMapping {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static Class clazz = ClassFile

    public static Map<Integer,String> javaMinorVersionToHuman = clazz.getFields().findAll { it.name.startsWith('JAVA_') }.collectEntries {[(it.get(null)):it.name]}


    public static Map<String,Integer> javaHumanToMinorVersion = MapUtils.invertMap( javaMinorVersionToHuman)

}
