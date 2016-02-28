package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class JavaServiceFinder {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void dumpSpi(Class resource) {
        dumpSpi(resource.getName())
    }

    static void dumpSpi(String resource) {
        Enumeration<URL> services = JrrClassUtils.currentClassLoader.getResources("META-INF/services/${resource}")
        List<URL> list = services.toList()
        log.info "${list}"
        list.each {
            log.info "${it}:\n${it.text}"
        }
    }


}
