package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs;

import java.util.logging.Logger;

@CompileStatic
class SshConsoleAdder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void addSshConsole(AddFilesToClassLoaderCommon adder) {
        adder.add GitReferences.sshConsole
        adder.add GitSomeRefs.sshConsole.childL('images/')
//        File baseDir = GitReferences.sshConsole.specOnly.resolveToFile()
//        adder.add new File(baseDir, 'images')
    }

}
