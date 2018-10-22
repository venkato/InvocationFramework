package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.ComplexGitRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustomRefsUrls
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterProjects
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SvnRefs;

import java.util.logging.Logger;

@CompileStatic
class IdeaRuntimeClassRefrences {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void addReferences(){
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(GitSomeRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(GitReferences)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(IfFrameworkSrcDirs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(IfFrameworkResourceDirs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(BaseDirSetting)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(ComplexGitRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(CustomRefsUrls)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JeditermBinRefs2)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterProjects)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(SvnRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(JrrStarterJarRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(JrrStarterVariables)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterJarRefs2)

    }

}
