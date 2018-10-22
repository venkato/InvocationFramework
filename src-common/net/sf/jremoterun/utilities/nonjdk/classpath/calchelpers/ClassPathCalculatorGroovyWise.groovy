package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.GradleRepoHashToFileMap
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.GrapeRepoHashToFileMap
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.MavenRepoHashToFileMap
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.Logger

@CompileStatic
class ClassPathCalculatorGroovyWise extends ClassPathCalculatorGitRefSup {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GradleRepoHashToFileMap gradleRepoHashToFileMap
    MavenRepoHashToFileMap mavenRepoHashToFileMap
    GrapeRepoHashToFileMap grapeRepoHashToFileMap
    LongTaskInfo longTaskInfo;

    ClassPathCalculatorGroovyWise(LongTaskInfo longTaskInfo) {
        this.longTaskInfo = longTaskInfo;
    }


    void calcMavenCache() {
        if (mavenCommonUtils.fileType == MavenFileType2.binary.fileSuffix) {
            if (mavenRepoHashToFileMap == null && mavenCommonUtils.mavenDefaultSettings.mavenLocalDir != null && mavenCommonUtils.mavenDefaultSettings.mavenLocalDir.exists()) {
                mavenRepoHashToFileMap = new MavenRepoHashToFileMap()
                mavenRepoHashToFileMap.mavenCommonUtils = mavenCommonUtils
                mavenRepoHashToFileMap.init(longTaskInfo)

            }
            if (gradleRepoHashToFileMap == null && mavenCommonUtils.mavenDefaultSettings.gradleLocalDir != null && mavenCommonUtils.mavenDefaultSettings.gradleLocalDir.exists()) {
                gradleRepoHashToFileMap = new GradleRepoHashToFileMap()
                gradleRepoHashToFileMap.mavenCommonUtils = mavenCommonUtils
                gradleRepoHashToFileMap.init(longTaskInfo)
            }
            if (grapeRepoHashToFileMap == null && mavenCommonUtils.mavenDefaultSettings.grapeLocalDir != null && mavenCommonUtils.mavenDefaultSettings.grapeLocalDir.exists()) {
                grapeRepoHashToFileMap = new GrapeRepoHashToFileMap()
                grapeRepoHashToFileMap.mavenCommonUtils = mavenCommonUtils
                grapeRepoHashToFileMap.init(longTaskInfo)
            }
        }
    }


    long getTotalInitTime() {
        long totalTime = 0;
        if (mavenRepoHashToFileMap != null) {
            totalTime += mavenRepoHashToFileMap.initTime
        }
        if (gradleRepoHashToFileMap != null) {
            totalTime += gradleRepoHashToFileMap.initTime
        }
        if (grapeRepoHashToFileMap != null) {
            totalTime += grapeRepoHashToFileMap.initTime
        }
        return totalTime;
    }

    @Override
    MavenId tryFindMavenIdFromJar(File jarFile) {
        calcMavenCache()
        MavenId mavenId = super.tryFindMavenIdFromJar(jarFile)
        String shainFile = calcSha1ForFile(jarFile)
        if (mavenId == null) {
            if (jarFile.file) {
                mavenId = findMavenIdByHash(jarFile)
            }
        } else {
            File fileInMaven = mavenCommonUtils.findMavenOrGradle(mavenId)
            if (fileInMaven == null) {
                return mavenId
            }
            String shaInMaven = calcSha1ForFile(fileInMaven)

            if (shainFile != shaInMaven) {
                onDifferentHashInFileAndInMavenFile(jarFile, fileInMaven, mavenId, shainFile, shaInMaven)
                return null
            }

        }
        return mavenId
    }

    MavenId findMavenIdByHash(File jarFile) {
        String shainFile = calcSha1ForFile(jarFile)
        MavenId mavenId = null
        if (mavenRepoHashToFileMap != null) {
            mavenId = mavenRepoHashToFileMap.hash2MavenIdMap.get(shainFile)
            if (mavenId != null) {
                if (onMavenIdFoundByHash(jarFile, mavenId)) {
                    return mavenId
                }
            }
        }
        if (gradleRepoHashToFileMap != null) {
            mavenId = gradleRepoHashToFileMap.hash2MavenIdMap.get(shainFile)
            if (mavenId != null) {
                if (onMavenIdFoundByHash(jarFile, mavenId)) {
                    return mavenId
                }
            }
        }
        if (grapeRepoHashToFileMap != null) {
            mavenId = grapeRepoHashToFileMap.hash2MavenIdMap.get(shainFile)
            if (mavenId != null) {
                if (onMavenIdFoundByHash(jarFile, mavenId)) {
                    return mavenId
                }
            }
        }

        return mavenId
    }


    boolean onMavenIdFoundByHash(File jarFile, MavenId mavenId) {
        File fileInMaven = mavenCommonUtils.findMavenOrGradle(mavenId)
        if (fileInMaven == null) {
            log.info "stange for : ${mavenId} by hash ${jarFile}"
            return false
        }
        if (fileInMaven.length() != jarFile.length()) {
            log.info "differnt fileInMaven size with same hash : ${mavenId} ${jarFile} ${fileInMaven}"
            return false
        }
        log.info "found ${mavenId} by hash ${jarFile}"
        return true
    }

    void onDifferentHashInFileAndInMavenFile(File jarFile, File fileInMaven, MavenId mavenId, String sha1InJar, String sha1InMaven) {
        log.info "Different sha1 for ${jarFile} ${mavenId}"
    }


    class FileAndPosition {
        File file;
        int posotion;
    }

    static void calcSha1ForFilePrint(File file) {
        assert file.exists()
        String sha1ForFile = calcSha1ForFile(file)
        log.info "${sha1ForFile}"
    }

    static String calcSha1ForFile(File file) {
        FileInputStream fis = new FileInputStream(file)
        try {
            String hex = DigestUtils.sha1Hex(fis)
            return hex;
        } finally {
            fis.close()
        }
    }


    List removeFileDup(List files) {
        List res = []
        Map<String, List<FileAndPosition>> map = [:]
        int currentPosotion = 0;
        files.each {
            switch (it) {
                case { it instanceof File }:
                    boolean needAdd = false
                    File file = (File) it;
                    if (file.isFile()) {
                        String hex = calcSha1ForFile(file)
                        List<FileAndPosition> get = map.get(hex)
                        if (get == null) {
                            FileAndPosition fileAndPosition = new FileAndPosition()
                            fileAndPosition.file = file
                            fileAndPosition.posotion = currentPosotion
                            map.put(hex, [fileAndPosition])
                            needAdd = true
                        } else {
                            if (sameFiles(get, file)) {
                                log.info "file with the same sha1 exists : ${file} , added ${get.first().file} at position ${get.first().posotion}"
                            } else {
                                FileAndPosition fileAndPosition = new FileAndPosition()
                                fileAndPosition.file = file
                                fileAndPosition.posotion = currentPosotion
                                get.add(fileAndPosition)
                                needAdd = true
                            }
                        }
                    } else {
                        needAdd = true
                    }

                    if (needAdd) {
                        res.add(file)
                        currentPosotion++
                    }
                    break;
                default:
                    res.add(it)
                    currentPosotion++
                    break
            }

        }
        return res
    }


    boolean sameFiles(List<FileAndPosition> added, File inQ) {
        return true
    }

    @Override
    void saveHighestMavenIdSecond() {
        filesAndMavenIds = removeFileDup(filesAndMavenIds)
        super.saveHighestMavenIdSecond()
    }

//    @Deprecated
//    List saveHighestMavenId(List files) {
////        files = removeFileDup(files)
//        List res = super.saveHighestMavenId(files);
//        return res
//    }

}
