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
import net.sf.jremoterun.utilities.nonjdk.classpath.search.MavenSearch
import net.sf.jremoterun.utilities.nonjdk.ideadep.InternetAccess
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import org.joda.time.Period

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class FindMavenIdsAndDownload implements InternetAccess {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    MavenDefaultSettings mavenDefaultSettings = MavenDefaultSettings.mavenDefaultSettings

    int retryCount = 2


    MavenId findMavenIdsAndDownload4(File it, LongTaskInfo longTaskInfo) {
        String hash = ClassPathCalculatorGroovyWithDownloadWise.calcSha1ForFile(it);
        return findMavenIdsAndDownload3(it, hash, longTaskInfo)
    }

    void onException(Exception e, File file, String hex) {
        log.info("${file} ${e}")
    }

    MavenId findMavenIdsAndDownload3(File file, String hex, LongTaskInfo longTaskInfo) {
        longTaskInfo.startSubTask("Resolving maven id ${file}")
        MavenId mavenId = findMavenIdsAndDownload5(file, hex)
        longTaskInfo.finishSubTask()
        return mavenId
    }

    MavenId findMavenIdsAndDownload5(File file, String hex) {
        try {
            List<String> response = MavenSearch.findMavenIdsAndDownload6(file, hex)
            switch (response.size()) {
                case 0:
                    log.info "not found for ${file.absolutePath}"
                    break;
                case 1:
                    String mavenId2 = response.first()
                    log.info "found ${mavenId2}"
                    MavenId m = new MavenId(mavenId2)
                    mavenDefaultSettings.mavenDependenciesResolver.resolveAndDownloadDeepDependencies(m, true, false)
                    return m;
                    break;
                default:
                    log.info "found many ${response}"
            }
        } catch (Exception e) {
            log.log(Level.INFO, "failed download ${file} ${e}", e)
        }
        return null

    }




}