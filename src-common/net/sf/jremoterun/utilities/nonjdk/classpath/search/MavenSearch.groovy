package net.sf.jremoterun.utilities.nonjdk.classpath.search

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWise
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import org.joda.time.Period

import java.util.logging.Logger


@CompileStatic
class MavenSearch {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static JsonSlurper slurper = new JsonSlurper()

    static List<String> findMavenIdsAndDownload7(File file) {
        String hash = ClassPathCalculatorGroovyWithDownloadWise.calcSha1ForFile(file);
        return findMavenIdsAndDownload6(file, hash)
    }

    static List<String> findMavenIdsAndDownload6(File file, String hex) {
        URL url = new URL("http://search.maven.org/solrsearch/select?q=1:%22${hex}%22&rows=3&wt=json")
        Object result = slurper.parse(url)
        if (result == null) {
            throw new IllegalStateException("empty reply for ${file} ${hex}")
        }
        List<String> response = MavenResponseParser.parseResponse(result);

        return response
    }


    static List<MavenId> findMavenIdsAllArtifactsWithGroupId(String groupId, int maxResultCount) {
        assert !groupId.contains(':')
        String groupId2 = URLEncoder.encode(groupId, "UTF8")
        URL url = new URL("http://search.maven.org/solrsearch/select?q=g:%22${groupId2}%22&rows=${maxResultCount}&wt=json")
        java.lang.Object result = slurper.parse(url)
        assert result != null: url
        List<MavenId> response = MavenResponseParser.parseAllWithGroupLatestResponse(result);
        return response;

    }

    static List<MavenId> findMavenIdsAllArtifactsWithGroupId(String groupId) {
        return findMavenIdsAllArtifactsWithGroupId(groupId, 30)
    }

    static void findNonEmptyMavenIdsAllArtifactsWithGroupId(AddFilesToClassLoaderCommon adder, String groupId) {
        List<MavenId> mavenIds = findMavenIdsAllArtifactsWithGroupId(groupId)
        MavenDependenciesResolver dependenciesResolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        mavenIds = mavenIds.findAll {
            dependenciesResolver.resolveAndDownloadDeepDependencies(it, false, true).find { MavenId m -> adder.mavenCommonUtils.findMavenOrGradle(m) != null } != null
        }
        adder.addAllWithDeps mavenIds

    }


    static void findMavenIdsAndDownload1(File groovyFile) {
        LongTaskInfo longTaskInfo = new LongTaskInfo()
        ClassPathCalculatorGroovyWise calculatorGroovyWise = new ClassPathCalculatorGroovyWise(longTaskInfo)
        calculatorGroovyWise.calcMavenCache()

        log.info "init time ${new Period(calculatorGroovyWise.getTotalInitTime()).toStandardSeconds().seconds} sec"
        calculatorGroovyWise.addFilesToClassLoaderGroovySave.addFromGroovyFile(groovyFile)
        calculatorGroovyWise.calcClassPathFromFiles12()
        List<File> files = (List) calculatorGroovyWise.filesAndMavenIds.findAll { it instanceof File }
        findMavenIdsAndDownload3(files, longTaskInfo)
    }

    static void findMavenIdsAndDownload3(List<File> files, LongTaskInfo longTaskInfo) {
        FindMavenIdsAndDownload d = new FindMavenIdsAndDownload()

        files.each {
//            log.info "resolving ${it.absolutePath}"
            d.findMavenIdsAndDownload4(it, longTaskInfo)
        }

    }

}
