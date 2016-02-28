package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class LibSaver2 extends ClassPathCalculatorGroovyWithDownloadWise{

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    Library library;
    OrderRootType orderRootType;

    LibSaver2(Library library, OrderRootType orderRootType, LongTaskInfo longTaskInfo) {
        super(longTaskInfo)
        this.library = library
        this.orderRootType = orderRootType
        if(OrderRootType.SOURCES ==  orderRootType){
            mavenCommonUtils.fileType = MavenFileType2.source.fileSuffix
        }
    }

    List<MavenId> saveClassPathFromIdeaLib() throws Exception {
        return saveClassPathFromIdeaLib4().findAll{it instanceof MavenId}
    }

    List<MavenId> saveClassPathFromIdeaLib4() throws Exception {
        saveClassPathFromIdeaLib2()
        calcClassPathFromFiles12()
        saveSettingMissingMaveIds();
        return filesAndMavenIds
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
        filesAndMavenIds.addAll(collect)
//        calcClassPathFromFiles12();
//        List files12 = filesAndMavenIds
//        files12.addAll(customSource)
//        files12 = files12.findAll { it != null }.unique()
//        return files12;
    }

}
