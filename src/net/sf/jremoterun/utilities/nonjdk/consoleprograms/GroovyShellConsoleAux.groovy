package net.sf.jremoterun.utilities.nonjdk.consoleprograms;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodFinderException
import net.sf.jremoterun.utilities.nonjdk.shell.GroovyShellConsole3;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GroovyShellConsoleAux extends GroovyShellConsole3{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    GroovyMethodFinderException e

    GroovyShellConsoleAux(GroovyMethodFinderException e) {
        this.e = e
    }

    @Override
    void displayWelcomeBanner2() {
        println(e)
        sh.runner.wrappedInputStream.insert("gmrp2.scriptNameInstance.")
    }

}
