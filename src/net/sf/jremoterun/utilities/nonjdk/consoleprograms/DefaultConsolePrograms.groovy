package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.depanalise.DependencyChecker

import java.util.logging.Logger

@CompileStatic
class DefaultConsolePrograms extends ConsolePrograms {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<String, ?> defaultShortcuts2 = [
            'sh'               : new ClRef('net.sf.jremoterun.utilities.nonjdk.shell.console.GroovyShellRunnerFromConsole'),
//            'sh'                          : new ClRef('net.sf.jremoterun.utilities.nonjdk.shell.GroovyShellRunner'),
            'k'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.ProgrammWinKill'),
            'p'                : ProxyConsolePrograms,
            'gen'              : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrConfigGenerator'),
            'addF'             : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.AddFilesToClassLoader'),
            'cm'               : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.ConsoleCompiler'),
            'jad'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrConsoleDecompiler'),
            'j2g'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.Java2GroovyConverter'),
            'gc2'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.GitCheckoutConsole'),



            'classAnalyze'       : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.ClassAnalyze'),
            'idea'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrIdeaGenerator'),
            'downloadMavenId'                : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.DropshipDown3'),
            'dependencyChecker': DependencyChecker,
            'classpathStatus'  : new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.ClasspathStatus'),
    ]

//    public static Map<String, Object> defaultShortcuts = new HashMap<>(map23)


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
    }


}
