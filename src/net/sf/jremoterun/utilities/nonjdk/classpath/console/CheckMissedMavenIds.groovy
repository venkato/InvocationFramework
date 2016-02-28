package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWise
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWithDownloadWise
import net.sf.jremoterun.utilities.nonjdk.classpath.search.FindMavenIdsAndDownload
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Logger

@CompileStatic
class CheckMissedMavenIds {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    MissedMavenIdsSettingsLoader missedMavenIdsSettingsLoader = new MissedMavenIdsSettingsLoader()

    void recheckMissedMavenIds() {
        createDependecyResolver();
        Map<File, String> loadsettings = ClassPathCalculatorGroovyWithDownloadWise.readNoMavenIdsFile()
        Collection<File> set = loadsettings.keySet()
        set = set.findAll { it.exists() }
        FindMavenIdsAndDownload mavenIdsAndDownload = new FindMavenIdsAndDownload()
        MavenCommonUtils mcu = new MavenCommonUtils()
        Map<File, String> missed2 = [:]
        LongTaskInfo longTaskInfo = new LongTaskInfo()
        set.each {
            String hash = ClassPathCalculatorGroovyWise.calcSha1ForFile(it)
            MavenId download4 = mavenIdsAndDownload.findMavenIdsAndDownload3(it, hash, longTaskInfo)
            if (download4 == null || mcu.findMavenOrGradle(download4) == null) {
                missed2.put(it, hash)
            } else {
                log.info "found ${download4}"
            }
        }
//        log.info "${missed2}"
        ClassPathCalculatorGroovyWithDownloadWise.saveSettingMissingMaveIdsS(missed2)
//        se.text = missedMavenIdsSettingsLoader.saveSettings(missed2)
    }

    void createDependecyResolver() {
        MavenDefaultSettings mds = MavenDefaultSettings.mavenDefaultSettings;
        if (mds.mavenDependenciesResolver == null) {
            IvyDepResolver2.setDepResolver()
        }
    }


    void findJarsInDir(File dir) {
        createDependecyResolver();
        MavenCommonUtils mcu = new MavenCommonUtils()
        List<File> set = dir.listFiles().toList().findAll { it.file }
        set = set.findAll { !it.name.contains('.source_') }
        FindMavenIdsAndDownload mavenIdsAndDownload = new FindMavenIdsAndDownload()
        LongTaskInfo longTaskInfo = new LongTaskInfo()
        Map<File, String> missed2 = [:]
        set.each {
            String hash = ClassPathCalculatorGroovyWise.calcSha1ForFile(it)
            MavenId download4 = mavenIdsAndDownload.findMavenIdsAndDownload3(it, hash, longTaskInfo)
            if (download4 == null || mcu.findMavenOrGradle(download4) == null) {
                missed2.put(it, hash)
            } else {
                log.info "found ${download4}"
            }
        }

    }


    void printClassLocation(Class clazz, File groovyClassPathFile) {
        URLClassLoader classLoader = new URLClassLoader(new URL[0], (ClassLoader) null)
        AddFilesToUrlClassLoaderGroovy files = new AddFilesToUrlClassLoaderGroovy(classLoader)
        files.addFromGroovyFile(groovyClassPathFile)
        println UrlCLassLoaderUtils.getClassLocation(clazz)
    }


}
