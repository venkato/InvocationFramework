package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.File2HashMapJsonSaver
import net.sf.jremoterun.utilities.nonjdk.classpath.search.FindMavenIdsAndDownload
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class ClassPathCalculatorGroovyWithDownloadWise extends ClassPathCalculatorGroovyWise {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    boolean enableFull = false;

//    MavenDependenciesResolver defaultMavenDepDownloader

    FindMavenIdsAndDownload findMavenIdsAndDownload = new FindMavenIdsAndDownload();

    //    File noMavenIdConfigFile = MissedMavenIdsSettingsLoader.noMavenIdFilesDefault

    public static File noMavenIdConfigJsonFile = new File(MavenDefaultSettings.mavenDefaultSettings.userHome, "jrr/configs/noMavenIds2.json")

    Map<File, String> noMavenIds

    ClassPathCalculatorGroovyWithDownloadWise(LongTaskInfo longTaskInfo) {
        super(longTaskInfo)
    }

    static Map<File, String> readNoMavenIdsFile(){
        return File2HashMapJsonSaver.readJson2(noMavenIdConfigJsonFile)
    }

    void loadMissedMavenids() {
        if (noMavenIds == null) {
            noMavenIds = [:]
//            if (noMavenIdConfigFile.exists()) {
//                noMavenIds = loader3.loadsettings(noMavenIdConfigFile)
//                noMavenIds.keySet().each {
//                    if (!it.exists()) {
//                        log.info("file not found ${it}")
//                    }
//                }
//            } else {
//            }
            noMavenIds.putAll(readNoMavenIdsFile())
        }
    }

    @Override
    void calcClassPathFromFiles12() throws Exception {
        loadMissedMavenids()
        super.calcClassPathFromFiles12()
    }

    void saveSettingMissingMaveIds(){
        saveSettingMissingMaveIdsS(noMavenIds)
    }

    static void saveSettingMissingMaveIdsS(Map<File, String> noMavenIds2){
        if (noMavenIds2.size() > 0) {
//            log.info("no maven ids saved ${noMavenIdConfigFile}")
//            noMavenIdConfigFile.text = loader3.saveSettings(noMavenIds)
            File2HashMapJsonSaver.saveToJson(noMavenIds2,noMavenIdConfigJsonFile)
        } else {
            log.info "noMavenIds is empty"
        }
    }

    Object filterOnAll3NormalizeMavenIds(Object object) throws Exception {
        if (object instanceof MavenId) {
            MavenId mavenId = object as MavenId
            File file = mavenCommonUtils.findMavenOrGradle(mavenId);
            if (file == null) {
                onMissingMavenId(mavenId)
            } else {
                File canonicalFile = file.canonicalFile
                if (canonicalFile.path != file.path) {
                    MavenId mavenId2 = mavenCommonUtils.detectMavenIdFromFileName(canonicalFile)
                    if (mavenId2 == null) {
                        throw new Exception("failed resolve maven id from file : ${canonicalFile.absolutePath},  which derived from maven id ${mavenId}")
                    }
                    return mavenId2
                }
            }
        }
        return object;
    }

    @Override
    String saveClassPath7(List files) throws Exception {
        saveSettingMissingMaveIds()
        return super.saveClassPath7(files)
    }

    @Override
    void onMissingMavenId(File file, MavenId mavenId) {
//        if(enableFull){
        loadMissedMavenids();
        String hash = calcSha1ForFile(file);
        if (noMavenIds.get(file) == hash) {
            super.onMissingMavenId(file, mavenId)
        }
        if(mavenCommonUtils.mavenDefaultSettings.mavenDependenciesResolver==null){
            throw new IllegalStateException("dep resolve was not set")
        }
        try {
            mavenCommonUtils.mavenDefaultSettings.mavenDependenciesResolver.resolveAndDownloadDeepDependencies(mavenId, false, false)
        } catch (Exception e) {
            log.log(Level.INFO, "${mavenId} ${file}", e)
            noMavenIds.put(file, hash)
            super.onMissingMavenId(file, mavenId)
        }
//        }else {
//            super.onMissingMavenId(file, mavenId)
//        }
    }

    @Override
    void onMissingMavenId(MavenId mavenId) {
        try {
            mavenCommonUtils.mavenDefaultSettings.mavenDependenciesResolver.resolveAndDownloadDeepDependencies(mavenId, false, false)
        } catch (Exception e) {
            log.log(Level.INFO, "${mavenId}", e)
            super.onMissingMavenId(mavenId)
        }
    }

    @Override
    MavenId tryFindMavenIdFromJar(File file) {
//        log.info "d ${file}"
        MavenId res = super.tryFindMavenIdFromJar(file)
//        log.info "d ${res} ${file}"
        if (res == null) {
            if(mavenCommonUtils.isParent(MavenDefaultSettings.mavenDefaultSettings.mavenLocalDir, file)){
                return null
            }
            if(mavenCommonUtils.isParent(MavenDefaultSettings.mavenDefaultSettings.gradleLocalDir, file)){
                return null
            }
            if(mavenCommonUtils.isParent(MavenDefaultSettings.mavenDefaultSettings.grapeLocalDir, file)){
                return null
            }
            return downloanFromMavenRepo(file)
//            mavenCommonUtils.detectMavenIdFromFileName(null)
        }
        return res;
    }


    MavenId downloanFromMavenRepo(File file){
//        log.info "d ${file}"
        String hash = calcSha1ForFile(file);
        if (noMavenIds.get(file) == hash) {
return null
        } else {
            log.info "resolving in web : ${file}"
            MavenId res = findMavenIdsAndDownload.findMavenIdsAndDownload3(file, hash,longTaskInfo)
            if (res == null) {
                 noMavenIds.put(file, hash)
            }
            return res
        }

    }
}
