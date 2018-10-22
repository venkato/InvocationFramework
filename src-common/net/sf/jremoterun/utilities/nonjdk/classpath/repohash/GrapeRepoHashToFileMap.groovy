package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.Logger

@CompileStatic
class GrapeRepoHashToFileMap {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Map<String, List> sha1Dups = [:]
    Map<String, MavenId> hash2MavenIdMap = [:]
    Map<String, File> hash2FileMap = [:]
    MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
    Map<MavenId, String> mavenId2HashMap = [:]
    long initTime;


    public
    static ToFileRef2 noMavenIdFilesJsonDefault = BaseDirSetting.baseDirSetting.childL("configs/ivy_hash_cache2.json")


    void init(LongTaskInfo longTaskInfo) {
        Date start = new Date()
        Map<File, String> fileCache3 = [:]

        fileCache3.putAll(File2HashMapJsonSaver.readJson2(noMavenIdFilesJsonDefault.resolveToFile()))

        mavenCommonUtils.mavenDefaultSettings.grapeLocalDir.eachFileRecurse(FileType.FILES, {

            String name = it.name;
            longTaskInfo.setCurrentTask("analizing ${it}")
            if (name.endsWith(".jar") && !name.endsWith("-sources.jar")) {
//                String sha1 = it.parentFile.name
//                hash2FileMap.put(sha1, it)
                String sha1;
                sha1 = fileCache3.get(it)
                if (sha1 == null) {
                    FileInputStream fis = new FileInputStream(it)
                    sha1 = DigestUtils.sha1Hex(fis)
                    fis.close()
                    fileCache3.put(it, sha1)
                }
                MavenId mi = mavenCommonUtils.detectMavenIdFromFileNameInGrapeDir(it, false)
                if (mi != null) {

//                    hash2FileMap.put(sha1, it)
                    mavenId2HashMap.put(mi, sha1)
                    if (sha1Dups.containsKey(sha1)) {
                        sha1Dups.get(sha1).add(it)
                    } else {
                        MavenId mavenIdBefore = hash2MavenIdMap.put(sha1, mi)
                        if (mavenIdBefore != null) {
                            List dupInfo = [mavenIdBefore, it]
                            sha1Dups.put(sha1, dupInfo)
                            hash2MavenIdMap.remove(sha1)
                        }
                    }

                } else {
                    log.info "failed detect maven id : ${it}"
                }
            }
        })
        initTime = System.currentTimeMillis() - start.time
        File2HashMapJsonSaver.saveToJson(fileCache3, noMavenIdFilesJsonDefault.resolveToFile())
    }


}
