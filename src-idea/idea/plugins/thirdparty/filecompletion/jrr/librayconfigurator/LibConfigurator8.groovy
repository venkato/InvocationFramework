package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.TransactionGuard
import com.intellij.openapi.application.TransactionGuardImpl
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.NewVirtualFile
import com.intellij.openapi.vfs.newvfs.persistent.PersistentFS
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.ideasdk.IdeaSdkToLibAdapter3
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources

import javax.swing.SwingUtilities
import java.util.logging.Logger

@CompileStatic
class LibConfigurator8 extends AddFileWithSources {

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    public Library.ModifiableModel model

    Set<String> sources = [];
    Set<String> binaries = [];

    LibConfigurator8(Library.ModifiableModel model) {
        downloadSources = true;
        this.model = model
        sources = model.getUrls(OrderRootType.SOURCES).toList().toSet();
        binaries = model.getUrls(OrderRootType.CLASSES).toList().toSet();
    }

//    void prepare(String libName) {
//        Library mylib = findLibarary(libName);
//        model = mylib.getModifiableModel()
//    }

    void commit() {
        model.commit()
    }

//    Library findLibarary(String libName) {
//        LibraryTablesRegistrar registrar = LibraryTablesRegistrar.getInstance();
//        LibraryTable libraryTable = registrar.getLibraryTable();
//        Library mylib = libraryTable.getLibraries().find { it.name == libName };
//        if (mylib == null) {
//            throw new Exception("Library ${libName} not found : ${libraryTable.getLibraries().toList().collect { it.name }}")
//        }
//        return mylib
//    }
//

    @Override
    void addSourceS(String source) throws Exception {
        model.addRoot(source, OrderRootType.SOURCES);
    }

    public static void submitTr(Runnable r) {
        ObjectWrapper<Throwable> exc = new ObjectWrapper(null);
        Runnable r3 = {
            try {
                r.run()
            } catch (Throwable e) {
                log.info "${e}"
                exc.object = e;
            }
        }

        TransactionGuard instance = TransactionGuard.getInstance();
        log.info "instance.contextTransaction = ${instance.contextTransaction}"
        boolean allowed = ApplicationManager.getApplication().isWriteAccessAllowed()
        log.info "wite access ${allowed}"
        if (instance.contextTransaction == null) {
            Runnable r2 = {
                instance.submitTransactionAndWait {
                    ApplicationManager.getApplication().runWriteAction(r3)
                }
            }
            new Thread(r2).start()
        } else {
            if (allowed) {
                r.run()
            } else {
                ApplicationManager.getApplication().invokeLaterOnWriteThread(r3)
                //ApplicationManager.getApplication().runWriteAction(r3)
            }
        }
        if (exc.object != null) {
            throw exc.object
        }
    }


    void assertWriteActionAllowed() {
        assert SwingUtilities.eventDispatchThread
//        TransactionGuard instance = TransactionGuard.getInstance()
//        assert instance.getContextTransaction() !=null
    }


    private void assertWriteActionAllowed_old() {
//        if(! ApplicationManager.getApplication().isWriteAccessAllowed()){
//            throw new Exception('Wrong idea state 2, need restart idea')
//        }
        TransactionGuardImpl transactionGuard = ((TransactionGuardImpl) TransactionGuard.getInstance())
//        boolean myWritingAllowed = JrrClassUtils.getFieldValue(transactionGuard, 'myWritingAllowed')
//        if (!myWritingAllowed) {
//            throw new Exception('Wrong idea state 3, need restart idea')
//        }
        assert transactionGuard.getContextTransaction() != null
        transactionGuard.assertWriteActionAllowed();
    }


    String createLibUrl(File libraryPath) {
        libraryPath = libraryPath.canonicalFile
        String url = VfsUtil.pathToUrl(FileUtil.toSystemIndependentName(libraryPath.absolutePath.replace('\\', '/')));
        String url2 = url.replaceAll("file://", "jar://");
        if (libraryPath.isDirectory()) {
            return url;
        } else {
            return url2 + "!/"
        }
    }


    VirtualFile fileToVirtual(File file) {
        file = file.canonicalFile
        VirtualFile file1 = LocalFileSystem.getInstance().findFileByIoFile(file);
        assert file1 != null
        return file1;
    }

    @Override
    void addLibraryWithSource(File file, List<File> source) {
        file = file.canonicalFile
        String escapedJarURL = createLibUrl(file);
        if (binaries.contains(escapedJarURL)) {
            log.info "already contains ${file}"
        } else {
            model.addRoot(escapedJarURL, OrderRootType.CLASSES);
            binaries.add(escapedJarURL)
        }
        if (source != null) {
            source.each {
                addSourceFImpl(it)
            }
        }
    }

    @Override
    void addSourceFImpl(File source) {
//        VirtualFile file = fileToVirtual(source)
        source = source.getCanonicalFile()
        String escapedJarURL2 = createLibUrl(source);
        if (sources.contains(escapedJarURL2)) {
            log.info "already contains ${source}"
        } else {
            if (model instanceof IdeaSdkToLibAdapter3) {
                NewVirtualFile file1
                if (source.isDirectory()) {
                    // TODO doesn't work need receive instance of com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl
                    file1 = PersistentFS.getInstance().findRoot(source.absolutePath, LocalFileSystem.getInstance())
                } else {
                    file1 = PersistentFS.getInstance().findRoot(source.absolutePath, JarFileSystem.getInstance())
                }
                if (file1 == null) {
                    throw new RuntimeException("failed resolve virtual file from file : ${source}")
                }
//                IdeaSdkToLibAdapter3 modelIdeaSdk = (IdeaSdkToLibAdapter3) model;
//                VirtualFile file = fileToVirtual(source)
                model.addRoot(file1, OrderRootType.SOURCES)
            } else {
                model.addRoot(escapedJarURL2, OrderRootType.SOURCES);
            }
            sources.add(escapedJarURL2)
        }
    }

    void deleteallS(OrderRootType type) {
        List<String> urlsClasses = model.getUrls(type).toList()
        urlsClasses.each { model.removeRoot(it, type) }
    }

    void deleteAll() {
        deleteallS(OrderRootType.SOURCES)
        deleteallS(OrderRootType.CLASSES)
//        deleteallS(OrderRootType.DOCUMENTATION)
    }
}
