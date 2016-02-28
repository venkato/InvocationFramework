package net.sf.jremoterun.utilities.nonjdk.shell

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shell.core.GroovyShellRunner2
import org.codehaus.groovy.tools.shell.IO

import java.util.logging.Logger

@CompileStatic
class GroovyShellConsole3 extends GroovyShellRunner2{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GroovyShellConsole3() {
        super(new Binding ())
    }

    void runConsole() {
        customCreateListPackages();
        io = new IO()
//        setDebug()
        createGroovyShell()
//        log.info "cp3"
        shellStarted = this
        sh.run(null)
        log.info "finished"
        flushHistory()
    }

}
