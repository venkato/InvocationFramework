package net.sf.jremoterun.utilities.nonjdk.langimprovetesters

import groovy.transform.CompileStatic

import java.util.logging.Logger

@CompileStatic
class FieldAccessTester extends Script {

    private static final Logger log = Logger.getLogger(FieldAccessTester.name)

    @Override
    Object run() {
        fieldAccess()
        smartCast()
        return null
    }

    static void fieldAccess() {
        URLClassLoader cl = new URLClassLoader(new URL[0], ExtMethodsTester.classLoader)
//        Object pdcache = cl.@pdcache
//        pdcache.toString()
    }

    static void smartCast() {
        URL url = 'http://aaa.com' as URL
        url.toString()
    }
}
