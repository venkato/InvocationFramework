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

    public static JsonSlurper slurper = new JsonSlurper()
    public static String mavenSearchSite = 'https://search.maven.org/solrsearch'

    public static String quote ='%22'
    public static String space ='%20'
    public int maxResultCountDefault = 100
    String defaultAndQuery = """  AND p:"jar"    """.trim()

    List<String> findMavenIdsAndDownload7(File file) {
        String hash = ClassPathCalculatorGroovyWithDownloadWise.calcSha1ForFile(file);
        return findMavenIdsAndDownload6(file, hash)
    }

    List<String> findMavenIdsAndDownload6(File file, String hex) {
        URL url = new URL("${mavenSearchSite}/select?q=1:${quote}${hex}${quote}&rows=3&wt=json")
        Map result = slurper.parse(url) as Map
        if (result == null) {
            throw new IllegalStateException("empty reply for ${file} ${hex}")
        }
        List<String> response = MavenResponseParser.parseResponse(result);

        return response
    }

    static String convertQueryToUrl(String query){
        query =  query.replace(' ',space)
        query =  query.replace('"',quote)
        return query
    }




    Map findMavenIdsAllArtifactsWithGroupIdRaw(String groupId, int maxResultCount) {
        assert !groupId.contains(':')
        String groupId2 = URLEncoder.encode(groupId, "UTF8")
        String searchByGroupQuery = """  "${groupId2}" ${defaultAndQuery}    """.trim()
        searchByGroupQuery =  convertQueryToUrl(searchByGroupQuery)
        String fullQuery = "${mavenSearchSite}/select?q=g:${searchByGroupQuery}&rows=${maxResultCount}&wt=json"
        log.info "running : ${fullQuery} .."
        URL url = new URL(fullQuery)
        Map result = slurper.parse(url) as Map
        return result;
    }

    List<MavenId> findMavenIdsAllArtifactsWithGroupId(String groupId, int maxResultCount) {
        Map raw = findMavenIdsAllArtifactsWithGroupIdRaw(groupId, maxResultCount)
        List<MavenId> response = MavenResponseParser.parseAllWithGroupLatestResponse(raw);
        return response;
    }

    List<MavenId> findMavenIdsWithGroupIdAndHasJar(String groupId, int maxResultCount) {
        Map raw = findMavenIdsAllArtifactsWithGroupIdRaw(groupId, maxResultCount)
        List<MavenId> response = new MavenResponseParser().parseAllWithGroupLatestResponse2(raw);
        return response;
    }


    List<MavenId> findMavenIdsAllArtifactsWithGroupId(String groupId) {
        return findMavenIdsAllArtifactsWithGroupId(groupId, maxResultCountDefault)
    }

     void findNonEmptyMavenIdsAllArtifactsWithGroupId(AddFilesToClassLoaderCommon adder, String groupId) {
        List<MavenId> mavenIds = findMavenIdsAllArtifactsWithGroupId(groupId)
        MavenDependenciesResolver dependenciesResolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        mavenIds = mavenIds.findAll {
            dependenciesResolver.resolveAndDownloadDeepDependencies(it, false, true).find { MavenId m -> adder.mavenCommonUtils.findMavenOrGradle(m) != null } != null
        }
        adder.addAllWithDeps mavenIds
    }


    void findMavenIdsAndDownload1(File groovyFile) {
        LongTaskInfo longTaskInfo = new LongTaskInfo()
        ClassPathCalculatorGroovyWise calculatorGroovyWise = new ClassPathCalculatorGroovyWise(longTaskInfo)
        calculatorGroovyWise.calcMavenCache()

        log.info "init time ${new Period(calculatorGroovyWise.getTotalInitTime()).toStandardSeconds().seconds} sec"
        calculatorGroovyWise.addFilesToClassLoaderGroovySave.addFromGroovyFile(groovyFile)
        calculatorGroovyWise.calcClassPathFromFiles12()
        List<File> files = (List) calculatorGroovyWise.filesAndMavenIds.findAll { it instanceof File }
        findMavenIdsAndDownload3(files, longTaskInfo)
    }

    void findMavenIdsAndDownload3(List<File> files, LongTaskInfo longTaskInfo) {
        FindMavenIdsAndDownload d = new FindMavenIdsAndDownload()

        files.each {
//            log.info "resolving ${it.absolutePath}"
            d.findMavenIdsAndDownload4(it, longTaskInfo)
        }

    }

}
