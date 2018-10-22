package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.ConsoleSymbols
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2

import java.util.logging.Logger

@CompileStatic
class ConsolePrograms extends GroovyRunnerConfigurator2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.instance

    public Map<String, ClRef> classMap = [:]

    void printhelp() {
        System.out.println("Available shortcuts :")
        classMap.each {
            System.out.println("${it.key}\t: ${it.value}")
        }

    }

    @Override
    void doConfig() {
        runImpl()
    }

    void runImpl(){
        SetConsoleColoring.setConsoleColoringNoRedirect()
        GroovyMethodRunnerParams2.gmrp2.groovyMethodRunner2.methodNotFound = new MethodNotFoundRunShell()
        boolean print;
        if (gmrp.args.size() < 1) {
            printhelp()
            System.exit(1)
        } else {

        }
        String arg3 = getFirstParam()
        if (arg3 == ConsoleSymbols.question.s) {
            printhelp()
            System.exit(0)
        }
        if (arg3 == 'p') {
            //gmrp.args.remove(0)
            removeFirstParam()
            new ProxyConsolePrograms().run()
        } else {
            ClRef string = classMap.get(arg3)
            if (string == null) {
                log.severe("shortcut not found : ${arg3}")
//                log.severe( ConsoleUtils.hilightOutput("shortcut not found : ${arg3}").toString())
                printhelp()
                System.exit(1)
            } else {
                removeFirstParam()
//                gmrp.args.remove(0)
                GroovyMethodRunnerParams2.gmrp2.mainClass = string
//                gmrp. = string;
            }
        }
//        log.info("hi")
    }


    void addProgram2(String acronym, Object impl) {
        if(impl instanceof Class){
            addProgram(acronym,impl)
        }else
        if(impl instanceof ClRef){
            addProgram(acronym,impl)
        }else {
            throw new IllegalStateException("uknown object ${impl} for ${acronym}")
        }
    }



    void addProgram(String acronym, ClRef impl) {
        ClRef put = classMap.put(acronym, impl)
        if (put != null) {
            throw new Exception("shotcust ${acronym} already exist, old = ${put} , new = ${impl}")
        }
    }

    void addProgram(String acronym, Class impl) {
        ClRef put = classMap.put(acronym, new ClRef(impl))
        if (put != null) {
            throw new Exception("shotcust ${acronym} already exist, old = ${put} , new = ${impl.name}")
        }
    }
}
