package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shell.console.GroovyShellRunnerFromConsoleWithMap

import java.util.logging.Logger

@CompileStatic
class DefaultConsolePrograms extends ConsolePrograms {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static String shortcutsShName = 'sh2'

    public static Map<String, ?> defaultShortcuts2 = [:    ]
    static {
        ConsoleProgramEnum.values().toList().each {defaultShortcuts2.put(it.name(),it.clRef)}
    }

    DefaultConsolePrograms() {
        defaultShortcuts2.each { addProgram2(it.key, it.value) }
    }

    static void addDefaultPrograms(Map progi){
        defaultShortcuts2.each {
            Object before = progi.put(it.key,it.value)
            if(before!=null){
                throw new IllegalArgumentException("Duplicate key : ${it.key} , before : ${before} , new : ${it.value}")
            }
        }
        GroovyShellRunnerFromConsoleWithMap groovyShellRunnerFromConsoleWithMap = new GroovyShellRunnerFromConsoleWithMap(progi)
        progi.put(shortcutsShName,groovyShellRunnerFromConsoleWithMap)
    }


}
