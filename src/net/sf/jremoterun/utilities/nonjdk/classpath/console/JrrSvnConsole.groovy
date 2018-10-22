package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import org.tmatesoft.svn.cli.svn.SVN;

import java.util.logging.Logger;

@CompileStatic
class JrrSvnConsole extends SVN implements ClassNameSynonym {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


}
