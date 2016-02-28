package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.*
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer4Sub
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
class LibSaver {

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    static MethodClosure addSourceFileMethod = (MethodClosure) AddFilesWithSourcesI.&addSourceF

    static MethodClosure addSourceCustomMethod = (MethodClosure) AddFilesWithSourcesI.&addSourceS

    ObjectWriter objectWriter = new ObjectWriter()

    void saveAllGlobalLibToDir(File dir,LongTaskInfo longTaskInfo) {
        assert dir.exists()
        LibraryTablesRegistrar registrar = LibraryTablesRegistrar.getInstance();
        LibraryTable libraryTable = registrar.getLibraryTable();
        libraryTable.getLibraries().toList().each {
            File libFile = new File(dir, it.name + '.groovy')
            saveClassPathFromIdeaLibToFile(it, libFile,longTaskInfo)
        }
    }

    String saveClassPathFromIdeaLibToFile(Library library, File outfile,LongTaskInfo longTaskInfo) throws Exception {
        outfile.text = saveClassPathFromIdeaLib(library,longTaskInfo)
    }


    String saveClassPathFromIdeaLib(Library library, LongTaskInfo longTaskInfo) throws Exception {
        LibSaver4 saver2 = new LibSaver4(library, OrderRootType.SOURCES,longTaskInfo);
        saver2.calcMavenCache()
//        saver2.defaultMavenDepDownloader = CreateClassLoaderProxy.classLoaderProxy.createDropshipDependencyResolvedInSeparetClassloader3()
        saver2.saveClassPathFromIdeaLib2()
        saver2.classPathCalculator.calcClassPathFromFiles12()
        List filesSources = saver2.classPathCalculator.filesAndMavenIds
        filesSources = filesSources.findAll { !(it instanceof MavenId) }
        LibSaver4 saver3 = new LibSaver4(library, OrderRootType.CLASSES,longTaskInfo);
//        saver3.defaultMavenDepDownloader = saver2.defaultMavenDepDownloader
        saver2.copyCaches(saver3)
        List filesBinary = saver3.saveClassPathFromIdeaLib4()
        String classpath2 = saveClassPath7(filesBinary, filesSources)
//        String classpath2 = strings.join('\r\n')
        return classpath2
    }

    String saveClassPath7(List files, List sources) throws Exception {
        Writer3 writer3 = new Writer4Sub()
        assert files != null
        ClassPathCalculatorSup2Groovy calculatorGroovy = new ClassPathCalculatorSup2Groovy();
        calculatorGroovy.buildHeader(writer3)
        calculatorGroovy.buildImport(writer3)
        writer3.addImport(AddFilesWithSourcesI)
        writer3.body.add "" as String
//        writer3.body.addAll calculatorGroovy.buildVar(writer3)
        writer3.body.add "" as String

        writer3.body.add "if(b instanceof ${AddFilesWithSourcesI.simpleName}){" as String
        writer3.body.add "  ${AddFilesWithSourcesI.simpleName} s = b as ${AddFilesWithSourcesI.simpleName}" as String
        writer3.body.addAll((List) sources.collect {convertSourceEl(it,writer3)
        })
        writer3.body.add "}" as String
        writer3.body.add "" as String


        writer3.body.addAll(files.collect {calculatorGroovy.convertEl(it,writer3)
        })
        return writer3.buildResult()
    }

    String convertSourceEl(Object el,Writer3 writer3 ){
        String s = objectWriter.writeObject(writer3,el)
        if (el instanceof File) {
            return "  s.${addSourceFileMethod.method} ${s}"
        }
        return "  s.${addSourceCustomMethod.method} '{s}"

    }


}
