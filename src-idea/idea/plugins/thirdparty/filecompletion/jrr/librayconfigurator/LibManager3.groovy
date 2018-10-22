package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IndexReadyListener
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl

import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import java.awt.Component
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class LibManager3 {


    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    IdeaLibManagerSwing dialog8;
//    Library library;
//    private TextFieldWithHistory txtDirectoryToSearch;

    LibManager3(IdeaLibManagerSwing dialog8) {
        this.dialog8 = dialog8
    }

    void addFileToLib(File fileToSave) {
        assert fileToSave.parentFile.exists()
        dialog8.txtDirectoryToSearch.addCurrentTextToHistory()
//        LibraryManagerHistory.getInstance().addDir(fileToSave.absolutePath.tr('\\', '/'))
//        if(dialog8.listStore.currentList.contains(fileToSave)){
//            log.info "file alread in list : ${fileToSave}"
//        }else {
        dialog8.listStore.currentList.add(0, fileToSave)
        dialog8.listStore.currentList = dialog8.listStore.currentList.unique()
        dialog8.listStore.saveList6()
//        }
    }

    void addSources(LibItem libItem, NewValueListener<List<MavenId>> valueListener) {
        NewValueListener valueListener4 = valueListener
        try {
//        File fileToSave = dialog8.txtDirectoryToSearch.getText() as File
//        assert fileToSave.parentFile.exists()
//        addFileToLib(fileToSave)


            SwingUtilities.invokeLater {
                Project project = OSIntegrationIdea.openedProject;
                Task task = new Task.Backgroundable(project, "Add sources ...", true) {
                    @Override
                    public void run(ProgressIndicator indicator) {
                        try {
                            Object result3 = addSources3(indicator, libItem)
                            if (valueListener4 != null) {
                                valueListener4.newValue(result3)
                            }
                        }catch(Throwable e){
                            JrrUtilities.showException("Failed add source", e)
                        }

                    }
                };
                ProgressManager.getInstance().run(task);
            }

//        log.info "add sources fine ${fileToSave}"
        } catch (Throwable e) {
            JrrUtilities.showException("Failed add source", e)
        }
    }

    List<MavenId> addSources3(ProgressIndicator progressIndicator, LibItem libItem) {
        IdeaClasspathLongTaskInfo longTaskInfo = new IdeaClasspathLongTaskInfo(progressIndicator)
        ManyReposDownloaderImpl resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as ManyReposDownloaderImpl
        IdeaIvyEvent ideaIvyEvent = new IdeaIvyEvent(longTaskInfo);
        resolver.addIvyListener(ideaIvyEvent)
        try {
            List<MavenId> result = addSources2(longTaskInfo, libItem);
            log.info("add source done for ${libItem.library.getName()}")
            return result;
        } finally {
            resolver.removeIvyListener(ideaIvyEvent)
        }

    }

    public static boolean tryDownloadSourcesDefault = true
    boolean tryDownloadSources  = tryDownloadSourcesDefault

    List<MavenId> addSources2(LongTaskInfo longTaskInfo, LibItem libItem) {
        // List<MavenId> noSourceInLocalSources = []
//        log.info "found maven sources libs : ${alreadedAddedSources2}"

        LibSaver4 ls = new LibSaver4(libItem.library, OrderRootType.CLASSES, longTaskInfo);
        ls.calcMavenCache()
//        ls.defaultMavenDepDownloader = CreateClassLoaderProxy.classLoaderProxy.createDropshipDependencyResolvedInSeparetClassloader3();
        List<MavenId> binWithMissingSources = ls.saveClassPathFromIdeaLib()


        LibSaver4 saver2 = new LibSaver4(libItem.library, OrderRootType.SOURCES, longTaskInfo);
        ls.copyCaches(saver2)
        List<MavenId> alreadedAddedSources2 = saver2.saveClassPathFromIdeaLib()

        log.info "found maven binaries libs : ${binWithMissingSources}"
        binWithMissingSources -= alreadedAddedSources2;
        log.info "found maven binaries without sources : ${binWithMissingSources}"

        MavenCommonUtils mavenCommonUtilsSrc = new MavenCommonUtils();
        mavenCommonUtilsSrc.fileType = MavenFileType2.source.fileSuffix

        binWithMissingSources = binWithMissingSources.sort()
        if(tryDownloadSources) {
            List<MavenId> allMissedSources = binWithMissingSources.findAll {
                mavenCommonUtilsSrc.findMavenOrGradle(it) == null
            }
            MavenDependenciesResolver resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            allMissedSources.each {
                longTaskInfo.setCurrentTask("${it} downloading source ..")
                resolver.resolveAndDownloadDeepDependencies(it,true,false)
            }

        }

        ls.noSourceInLocalSources.addAll(binWithMissingSources.findAll {
            mavenCommonUtilsSrc.findMavenOrGradle(it) == null
        })
        ls.noSourceInLocalSources.sort()
        log.info "no sources ${ls.noSourceInLocalSources}"


        binWithMissingSources.removeAll(ls.noSourceInLocalSources)
        if (binWithMissingSources.size() == 0) {
            SwingUtilities.invokeLater {
                String msg = ""
                if (ls.noSourceInLocalSources.size() > 0) {
                    msg = "No sources : ${ls.noSourceInLocalSources}, "
                }
                msg = "${msg}Can' add find more sources"
                Component component78 =null
                if(dialog8!=null){
                     component78 =  dialog8.txtDirectoryToSearch
                }
                JOptionPane.showMessageDialog(component78, msg)
            }
        } else {
            String msg;
            if (ls.noSourceInLocalSources.size() > 0) {
                msg = "No sources : ${ls.noSourceInLocalSources}, "
            }
            addSources4(binWithMissingSources, libItem, msg)
//            }
        }
        return binWithMissingSources
    }


    void addSources4(List<MavenId> binWithMissingSources, LibItem libItem, String msg) {
        MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
        mavenCommonUtils.fileType = MavenFileType2.source.fileSuffix
        List<File> collect = binWithMissingSources.collect { mavenCommonUtils.findMavenOrGradle(it) }
        log.info "ask to add : ${collect}"
        log.info "ask to add : ${binWithMissingSources}"

        Disposable disposable = new Disposable() {

            @Override
            void dispose() {
                log.info "action finished"
            }
        }

        Runnable r = {
            try {
                log.info "adding files"
                Library.ModifiableModel modifiableModel = libItem.library.getModifiableModel();
                LibConfigurator8 configurator8 = new LibConfigurator8(modifiableModel)
                configurator8.assertWriteActionAllowed()
                configurator8.model = modifiableModel
                collect.each { configurator8.addSourceFImpl(it) }
                configurator8.commit()
                log.info "commit done ${libItem.library.name}"
                log.info "${libItem.library.name} : files added ${collect.size()}"
            } catch (Throwable e) {
                log.info "${e}"
                JrrUtilities.showException("Lib managed failed", e);
            }
        }

//            SwingUtilities.invokeLater {
        msg = "${msg}Add ? ${binWithMissingSources}"
        Component component = dialog8?.txtDirectoryToSearch
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(component, msg)) {
            // r.run();
            log.info "cp 444"
            LibConfigurator8.submitTr(r);
        }
    }

    static void addCustomAny(LibItem libItem, NewValueListener<LibConfigurator8> binWithMissingSources) {

        Runnable r = {
            try {
                log.info "adding files"
                Library.ModifiableModel modifiableModel = libItem.library.getModifiableModel();
                LibConfigurator8 configurator8 = new LibConfigurator8(modifiableModel)
                configurator8.assertWriteActionAllowed()
                configurator8.model = modifiableModel
                binWithMissingSources.newValue(configurator8)
                configurator8.commit()
                log.info "commit done ${libItem.library.name}"
            } catch (Throwable e) {
                log.info "${e}"
                JrrUtilities.showException("Lib managed failed", e);
            }
        }

        LibConfigurator8.submitTr(r);
    }

    File checkFile(String fileToSaveOrig) {
        if (fileToSaveOrig == null) {
            log.info("error1")
            JOptionPane.showMessageDialog(null, "Select file")
            return null
        }
        fileToSaveOrig = fileToSaveOrig.trim()
        if (fileToSaveOrig == null) {
            log.info("error2")
            JOptionPane.showMessageDialog(null, "Select file")
            return null
        }
        File fileToSave = fileToSaveOrig as File
        if (fileToSave == null) {
            JOptionPane.showMessageDialog(null, "Select file")
            return null
        }
        if (fileToSave.parentFile == null) {
            JOptionPane.showMessageDialog(null, "Failed find parent for ${fileToSave}")
            return null
        }
        if (!fileToSave.parentFile.exists()) {
            JOptionPane.showMessageDialog(null, "Parent file not exist")
            return null
        }
        if (fileToSave.isDirectory()) {
            JOptionPane.showMessageDialog(null, "File is directory")
            return null
        }
//        if(!fileToSave.canRead()){
//            JOptionPane.showMessageDialog(null,"Can't read file")
//            return false
//        }
        return fileToSave
    }

    public void runImport(LibItem libItem) {
        try {

            File fileToSave = checkFile(dialog8.txtDirectoryToSearch.getText())
            if (fileToSave == null) {
                return
            }
            assert fileToSave.exists()
            assert fileToSave.file
            assert fileToSave.canRead()
            try {
                addFileToLib(fileToSave)
            }catch (Throwable e){
                JrrUtilities.showException("Failed remember choosed file ",e)
            }
            if (!fileToSave.exists()) {
//                JOptionPane.showMessageDialog(null, "File not found")
//                return
                throw new FileNotFoundException(fileToSave.absolutePath)
            }

            IdeaAddFileWithSources withSources =  IdeaAddFileWithSourcesFactory.defaultFactory.createAdded();
//        LibConfigurator8 configurator4 = new LibConfigurator8()
//        String libName = libItem.library.getName();
            Runnable r = {
                try {
                    LibConfigurator8 configurator9 = new LibConfigurator8(libItem.library.getModifiableModel())
                    log.info "deleting all libs .. "
                    configurator9.deleteAll()
                    configurator9.commit()
                    log.info "deleting all libs done "
                    SwingUtilities.invokeLater {
                        LibConfigurator8.submitTr {
                            try {
                                LibConfigurator8 configurator8 = new LibConfigurator8(libItem.library.getModifiableModel())
                                configurator8.assertWriteActionAllowed()
//                configurator8.prepare(libItem.library.getName())
                                configurator8.deleteAll();
                                withSources.binaries.each { configurator8.addLibraryWithSource(it, null) }
                                log.info("added source count : ${withSources.sources.size()}")
                                log.info("added files count : ${withSources.binaries.size()}")
                                withSources.sources.each { configurator8.addSourceF(it) }
                                withSources.sourcesString.each { configurator8.addSourceS(it) }
                                configurator8.assertWriteActionAllowed()
                                log.info "import fine ${fileToSave}"
                                configurator8.commit()

                                importFinishedFineUnsafe.run()
                                log.info "${libItem.library.getName()} commit done "

                            } catch (Throwable e) {
                                importFailed.newValue(e)
                            }
                        }
                    }
                } catch (Throwable e) {
                    importFailed.newValue(e);
                }
            }
            Runnable readyCallback = {
                try {
                    SwingUtilities.invokeLater {
                        LibConfigurator8.submitTr(r);
                    }
                } catch (Throwable e) {
                    importFailed.newValue(e)
                }
            }
            withSources.import2(OSIntegrationIdea.openedProject, fileToSave, readyCallback);
        } catch (Throwable e) {
            importFailed.newValue(e)
        }
    }

    public static Runnable importFinishedFineUnsafe = {
        Runnable r = {
            log.info "sleep for 1 sec .."
            Thread.sleep(1000);
            DumbService.getInstance(IndexReadyListener.getOpenedProject()).smartInvokeLater {
                log.info "index should be ready now"
                importFinishedFine.run()
            }
        }
        Thread thread = new Thread(r, 'Sleep after import classpath')
        thread.start()
    };

    public static volatile Runnable importFinishedFine = {};
    public static volatile NewValueListener<Throwable> importFailed = new NewValueListener<Throwable>() {
        @Override
        void newValue(Throwable throwable) {
            log.log(Level.SEVERE,"Failed import",throwable)
            JrrUtilities.showException("Failed import", throwable);
        }
    };

    public void runExport(LibItem libItem) {
        try {
//            File fileToSave = dialog8.txtDirectoryToSearch.getText() as File
            File fileToSave = checkFile(dialog8.txtDirectoryToSearch.getText())
            if (fileToSave == null) {
                return
            }
            boolean pass = !fileToSave.exists()
            if (!pass) {
                pass = JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "file exist, overwrite ? : ${fileToSave}")
            }
            log.info "pass ? ${pass}"
            if (pass) {

                assert fileToSave.parentFile.exists()
                try{
                addFileToLib(fileToSave)
                }catch (Throwable e){
                    JrrUtilities.showException("Failed remember choosed file ",e)
                }

                SwingUtilities.invokeLater {
                    Project project = OSIntegrationIdea.openedProject;
                    Task task = new Task.Backgroundable(project, "Export library ...", true) {
                        @Override
                        public void run(ProgressIndicator indicator) {
                            runExport2(indicator, fileToSave, libItem)
                        }
                    };
                    ProgressManager.getInstance().run(task);
                }
                log.info "export fine ${fileToSave}"
            }
        } catch (Throwable e) {
            JrrUtilities.showException("Failed export", e)
        }
    }

    void runExport2(ProgressIndicator progressIndicator, File fileToSave, LibItem libItem) {
        IdeaClasspathLongTaskInfo longTaskInfo = new IdeaClasspathLongTaskInfo(progressIndicator)
        IvyDepResolver2 resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as IvyDepResolver2
        IdeaIvyEvent ideaIvyEvent = new IdeaIvyEvent(longTaskInfo);
        resolver.ivy.eventManager.addIvyListener(ideaIvyEvent)
        try {
            LibSaver saver = new LibSaver()
            saver.saveClassPathFromIdeaLibToFile(libItem.library, fileToSave, longTaskInfo);
            log.info("export llib fine to ${fileToSave}")
        } finally {
            resolver.ivy.eventManager.removeIvyListener(ideaIvyEvent)
        }

    }

}
