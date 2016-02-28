package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams

import java.util.logging.Logger

@CompileStatic
class ProxyConsolePrograms implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.instance

    void printhelp() {
        System.out.println("Specify programm")
    }

    @Override
    void run() {
        log.info "console home programm"
        if (gmrp.args.size() < 2) {
            printhelp()
            System.exit(1)
        } else {

        }
//        gmrp.args.remove(0)
//        gmrp.args.remove(0)
//            gmrp.args[0] = string;
    }


}
