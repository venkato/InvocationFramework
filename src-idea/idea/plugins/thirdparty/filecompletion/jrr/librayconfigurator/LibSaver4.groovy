package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWise
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise
import net.sf.jremoterun.utilities.nonjdk.idea.set2.JarResolutionToMavenId
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Logger

@CompileStatic
class LibSaver4 {

    final ClassPathCalculatorWithAdder classPathCalculator;

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    List<MavenId> noSourceInLocalSources = []

    Library library;
    OrderRootType orderRootType;

    LibSaver4( Library library, OrderRootType orderRootType, LongTaskInfo longTaskInfo) {
        switch (SettingsRef.config.resolveJarsToMavenId){
            case JarResolutionToMavenId.none:
                classPathCalculator = new ClassPathCalculatorWithAdder(){
                    @Override
                    void onMissingMavenId(File file, MavenId mavenId) {
                        super.onMissingMavenId(file, mavenId)
                        noSourceInLocalSources.add(mavenId)
                    }
                };
                break;
            case JarResolutionToMavenId.max:
                classPathCalculator = new ClassPathCalculatorGroovyWithDownloadWise(longTaskInfo){
                    @Override
                    void onMissingMavenId(File file, MavenId mavenId) {
                        super.onMissingMavenId(file, mavenId)
                        noSourceInLocalSources.add(mavenId)
                    }
                };;
                break
            default: throw new IllegalStateException();
        }
        this.library = library
        this.orderRootType = orderRootType
        if (OrderRootType.SOURCES == orderRootType) {
            classPathCalculator.mavenCommonUtils.fileType = MavenFileType2.source.fileSuffix
        }
    }

    void copyCaches(LibSaver4 other){
        ClassPathCalculatorWithAdder calculator = other.classPathCalculator
        if (calculator instanceof ClassPathCalculatorGroovyWithDownloadWise) {
            ClassPathCalculatorGroovyWithDownloadWise  withDownloadWise= (ClassPathCalculatorGroovyWithDownloadWise) calculator;

            ClassPathCalculatorGroovyWithDownloadWise  thisWithDownloadWise= (ClassPathCalculatorGroovyWithDownloadWise) this.classPathCalculator;

            thisWithDownloadWise.gradleRepoHashToFileMap = withDownloadWise.gradleRepoHashToFileMap
            thisWithDownloadWise.mavenRepoHashToFileMap = withDownloadWise.mavenRepoHashToFileMap
            thisWithDownloadWise.grapeRepoHashToFileMap = withDownloadWise.grapeRepoHashToFileMap
        }
    }

    List<MavenId> saveClassPathFromIdeaLib() throws Exception {
        return saveClassPathFromIdeaLib4().findAll { it instanceof MavenId }
    }

    List<MavenId> saveClassPathFromIdeaLib4() throws Exception {
        saveClassPathFromIdeaLib2()
        classPathCalculator.calcClassPathFromFiles12()
        if (classPathCalculator instanceof ClassPathCalculatorGroovyWithDownloadWise) {
            ClassPathCalculatorGroovyWithDownloadWise wise = (ClassPathCalculatorGroovyWithDownloadWise) classPathCalculator;
            wise.saveSettingMissingMaveIds();
        }
        return classPathCalculator.filesAndMavenIds
    }

    void saveClassPathFromIdeaLib2() throws Exception {
        List<String> urls = library.getUrls(orderRootType).toList()
        urls = urls.findAll { it != null }.unique()
        List<String> customSource = []
        List<File> collect = (List) urls.collect {
            String res = it
            if (res.startsWith('jar://')) {
                res = res.replace('jar://', '')
                res = res.substring(0, res.length() - 2);
            } else if (res.startsWith('file://')) {
                res = res.replace('file://', '')
            } else {
                throw new UnsupportedOperationException("${it}")
            }
            File res2 = res as File
            if (res2.exists()) {
                return res2;
            }
            if (orderRootType == OrderRootType.CLASSES) {
                throw new IllegalStateException("failed resolve ${it}")
            }
            customSource.add(it)
            return null
        }
        classPathCalculator.filesAndMavenIds.addAll(collect)
//        calcClassPathFromFiles12();
//        List files12 = filesAndMavenIds
//        files12.addAll(customSource)
//        files12 = files12.findAll { it != null }.unique()
//        return files12;
    }

    void calcMavenCache() {
        if (classPathCalculator instanceof ClassPathCalculatorGroovyWise) {
            ClassPathCalculatorGroovyWise wise = (ClassPathCalculatorGroovyWise) classPathCalculator;
            wise.calcMavenCache()
        }
    }
}
