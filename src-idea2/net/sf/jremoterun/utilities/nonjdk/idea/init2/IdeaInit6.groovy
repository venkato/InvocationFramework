package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.InitPlugin2
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils

import java.util.logging.Logger

@CompileStatic
class IdeaInit6 implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        run2()
    }

    static void run2() throws Exception {
        InfocationFrameworkStructure.ifDir = GitReferences.ifFramework.resolveToFile()
        IdeaClasspathAdd.addCl.addAll IfFrameworkResourceDirs.all
        IdeaClasspathAdd.addCl.add GitReferences.rsta
        IdeaClasspathAdd.addCl.add GitReferences.rstaAutoCompetion
    }



}
