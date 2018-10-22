package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository

import java.util.logging.Logger

@CompileStatic
abstract class MavenManyReposDownloader implements MavenDependenciesResolver {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    abstract MavenDependenciesResolver getDefaultRepo();
    abstract MavenDependenciesResolver findRepo(IBiblioRepository repo);


    @Override
    List<MavenId> resolveAndDownloadDeepDependencies(MavenId mavenId, boolean downloadSource, boolean dep, IBiblioRepository repo) {
        MavenDependenciesResolver repo1 = findRepo(repo)
        return repo1.resolveAndDownloadDeepDependencies(mavenId,downloadSource,dep);
    }

    @Override
    void downloadSource(MavenId mavenId, IBiblioRepository repo) {
        MavenDependenciesResolver repo1 = findRepo(repo)
        repo1.downloadSource(mavenId)
    }

    @Override
    void downloadMavenPath(MavenPath path, boolean dep) {
            downloadMavenPath(path,dep);
    }

    @Override
    void downloadPathImplSpecific(String path, boolean dep) {
        downloadPathImplSpecific(path,dep);
    }


    @Override
    List<MavenId> resolveAndDownloadDeepDependencies(MavenId mavenId, boolean downloadSource, boolean dep) {
        return getDefaultRepo().resolveAndDownloadDeepDependencies(mavenId,downloadSource,dep);
    }

    @Override
    void downloadSource(MavenId mavenId) {
        getDefaultRepo().downloadSource(mavenId);
    }



    @Override
    File getMavenLocalDir() {
        return getDefaultRepo().getMavenLocalDir()
    }

    @Override
    URL getMavenRepoUrl() {
        return getDefaultRepo().getMavenRepoUrl()
    }





}
