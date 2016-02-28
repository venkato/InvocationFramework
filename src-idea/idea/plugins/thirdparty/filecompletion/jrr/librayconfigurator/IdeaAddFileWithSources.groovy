package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.JrrUtilities3
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy

import java.util.logging.Logger

@CompileStatic
class IdeaAddFileWithSources extends AddFileWithSources {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    Set<String> sourcesString = [];
    Set<File> sources = [];
    Set<File> binaries = [];

    IdeaAddFileWithSources() {
        downloadSources = true
    }

    @Override
    void addSourceFImpl(File source) {
        JrrUtilities3.checkFileExist(source)
        sources.add(source)
    }


    @Override
    void addSourceS(String source) throws Exception {
        sourcesString.add(source);
    }


    @Override
    void addLibraryWithSource(File file, List<File> source) {
        if (!binaries.contains(file)) {
            binaries.add(file)
        };
        if (source != null) {
            source.each {
                addSourceFImpl(it)
            }
        }
    }


    void import2(Project project, File groovyClassPath, Runnable readyCallback) {
        Task task = new Task.Backgroundable(project, "Import library ...", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {
                    prepare(groovyClassPath, indicator)
                    if (!indicator.isCanceled()) {
                        readyCallback.run()
                    }
                } catch (Throwable e) {
                    log.info "${e}"
                    JrrUtilities.showException("Lib managed failed", e);
                }
            }
        }
        ProgressManager.getInstance().run(task);
    }


    IdeaIvyEvent prepare(File groovyClassPath, ProgressIndicator indicator) {
        IdeaClasspathLongTaskInfo longTaskInfo = new IdeaClasspathLongTaskInfo(indicator)
        IvyDepResolver2 resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as IvyDepResolver2
        IdeaIvyEvent ideaIvyEvent = new IdeaIvyEvent(longTaskInfo);
        resolver.ivy.eventManager.addIvyListener(ideaIvyEvent)
        try {
            import22(groovyClassPath)
        } finally {
            resolver.ivy.eventManager.removeIvyListener(ideaIvyEvent)
        }
        return ideaIvyEvent
    }

    void import22(File groovyClassPath) {

        ClassPathCalculatorSup2Groovy cl = new ClassPathCalculatorSup2Groovy();
        cl.addFilesToClassLoaderGroovySave.addFromGroovyFile(groovyClassPath)
        cl.calcAndAddClassesToAdded(this)
        cl.javaSources.each {
            addSourceGeneric(it)
        }
    }


}
