package net.sf.jremoterun.utilities.nonjdk.langimprovetesters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass

import java.util.logging.Logger

@CompileStatic
class ExtMethodsTester extends Script {

    private static final Logger log = Logger.getLogger(ExtMethodsTester.name)

    @Override
    Object run() {
        fieldAccess()
        child()
        logTester()
//        log.info "all checks done"
        return null
    }

    static void fieldAccess() {
        File parent = new File("/a1")
        File child = new File("/a1/b")
        assert parent.isChildFile(child)
    }

    static void child() {
        File parent = new File("/a1")
        File child = parent.child("b")
        child.toString()
    }

    static void logTester(){
        log.debug("some msg")
    }

}
