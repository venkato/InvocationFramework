package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId

import java.util.logging.Logger

@Deprecated
@CompileStatic
class IdeaClassPathCalc2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static MavenCommonUtils mcu = new MavenCommonUtils()

//
    static File filterOnFile(File file) {
        if (file.path.replace('\\', '/').endsWith('/lib/tools.jar')) {
            return null
        }
        return file

    }


    static MavenId filterOnMavenIdIdea(MavenId mavenId) {
        String group = mavenId.groupId;
        String artifact = mavenId.artifactId
        switch (mavenId) {
            case { artifact == 'slf4j-api' }:
            case { group == 'log4j' }:
            case { group == 'net.java.dev.jna' }:
            case { group == 'commons-logging' }:
                return null
            default:
                break
        }
        return mavenId
    }

//    static List files = [
//        mcu.getToolsJarFile(),
//    ]

}
