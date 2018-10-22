package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo

import java.util.logging.Logger

@CompileStatic
class MavenRepoHashToFileMap {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Map<String, List> sha1Dups = [:]
    Map<String, MavenId> hash2MavenIdMap = [:]
    Map<MavenId,String> mavenId2HashMap = [:]
    Map<String, File> hash2FileMap = [:]
    MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
    long initTime;


    void init(LongTaskInfo longTaskInfo){
        if(longTaskInfo==null){
            throw new NullPointerException('longTaskInfo is null')
        }
        Date start = new Date()


        MavenDefaultSettings.mavenDefaultSettings.mavenLocalDir.eachFileRecurse(FileType.FILES, {

            String name = it.name;
            longTaskInfo.setCurrentTask("analizing ${it}")
            if (name.endsWith(".jar") && !name.endsWith("-sources.jar")) {
                File sha1File = new File(it.parentFile, "${name}.sha1");
                if (sha1File.exists()) {
                    String sha1 = sha1File.text
                    hash2FileMap.put(sha1,it)
                    MavenId mi = mavenCommonUtils.detectMavenIdFromFileNameInMavenDir(it,false)
                    if (mi != null) {
                        mavenId2HashMap.put(mi,sha1)
                        if (sha1Dups.containsKey(sha1)) {
                            sha1Dups.get(sha1).add(it)
                        } else {
                            MavenId mavenIdBefore = hash2MavenIdMap.put(sha1, mi)
                            if (mavenIdBefore != null) {
                                List dupInfo = [mavenIdBefore,it]
                                sha1Dups.put(sha1,dupInfo)
                            }
                        }
                    }
                }
            }
        })
        initTime = System.currentTimeMillis() - start.time
    }

}
