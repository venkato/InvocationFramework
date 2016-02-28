package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import org.apache.ivy.plugins.resolver.IBiblioResolver

import java.util.logging.Logger

@CompileStatic
class DropshipDown3 implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void downloadD(MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        IvyDepResolver2 ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as IvyDepResolver2
        ivyDepResolver2.setLogLevel(info?
                org.apache.ivy.util.Message.MSG_INFO:org.apache.ivy.util.Message.MSG_DEBUG)
        if(!info) {
            ivyDepResolver2.setLogDebug()
        }
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies)
        log.info "${dependencies}"

    }



    void downloadD2(MavenRepositoriesEnum mavenRepo, MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        IvyDepResolver2 ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as IvyDepResolver2
        ivyDepResolver2.setLogLevel(
                info?
                        org.apache.ivy.util.Message.MSG_INFO:org.apache.ivy.util.Message.MSG_DEBUG)
        if(!info) {
            ivyDepResolver2.setLogDebug()
        }
        ivyDepResolver2.addResolverAfterInit(mavenRepo)
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies)
        log.info "${dependencies}"

    }

    void downloadD(String mavenRepo, MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        IvyDepResolver2 ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as IvyDepResolver2
        ivyDepResolver2.setLogLevel(
                info?
                        org.apache.ivy.util.Message.MSG_INFO:org.apache.ivy.util.Message.MSG_DEBUG)
        if(!info) {
            ivyDepResolver2.setLogDebug()
        }
        IBiblioResolver custom = ivyDepResolver2.buildPublicIbiblioCustom('custom', mavenRepo)
        ivyDepResolver2.addResolverAfterInit(custom)
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies)
        log.info "${dependencies}"
    }

    /**
     * Fine maven id by file
     * @See net.sf.jremoterun.utilities.nonjdk.classpath.FindMavenIdsAndDownload#findMavenIdsAndDownload7
     */
    private void doNothing1(){}

}
