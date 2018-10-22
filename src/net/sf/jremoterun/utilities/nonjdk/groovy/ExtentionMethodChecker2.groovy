package net.sf.jremoterun.utilities.nonjdk.groovy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass
import org.codehaus.groovy.runtime.m12n.ExtensionModuleRegistry
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl

import java.util.logging.Logger

@CompileStatic
class ExtentionMethodChecker2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void check() {
        File f = GitSomeRefs.ifFramework.childL('resources/tcpmon/extMethodTester.groovy').resolveToFile();
        RunnableFactory.createRunner(f).run();
    }
}
