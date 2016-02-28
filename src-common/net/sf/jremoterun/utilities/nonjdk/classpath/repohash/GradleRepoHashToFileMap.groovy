package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Logger

@CompileStatic
class GradleRepoHashToFileMap {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Map<String, List> sha1Dups = [:]
    Map<String, MavenId> hash2MavenIdMap = [:]
    Map<String, File> hash2FileMap = [:]
    MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
    Map<MavenId, String> mavenId2HashMap = [:]
    long initTime;


    void init(LongTaskInfo longTaskInfo) {
        Date start = new Date()
        mavenCommonUtils.mavenDefaultSettings.gradleLocalDir.eachFileRecurse(FileType.FILES, {

            String name = it.name;
            longTaskInfo.setCurrentTask("analysing ${it}")
            if (name.endsWith(".jar") && !name.endsWith("-sources.jar")) {
                String sha1 = it.parentFile.name
                hash2FileMap.put(sha1, it)
                MavenId mi = mavenCommonUtils.detectMavenIdFromFileNameInGradleDir(it,false)
                if (mi != null) {
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

                }
            }
        })
        initTime = System.currentTimeMillis() - start.time
    }

}
