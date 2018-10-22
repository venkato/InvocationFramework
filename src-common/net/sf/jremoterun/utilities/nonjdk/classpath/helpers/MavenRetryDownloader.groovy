package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository

import java.util.logging.Logger
@CompileStatic
class MavenRetryDownloader implements MavenDependenciesResolver {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    int retryCount = 2


    MavenDependenciesResolver impl;

    MavenRetryDownloader(MavenDependenciesResolver impl) {
        this.impl = impl
    }

    void onException(Exception e) {
        Throwable rootException = JrrUtils.getRootException(e);
        if(rootException.getClass().getName() == 'org.sonatype.aether.transfer.ArtifactNotFoundException'){
            throw e;
        }else{
            log.info("${e}")
        }
    }

    @Override
    void downloadMavenPath(MavenPath path, boolean dep) {

        int leftRetry = retryCount
        while (true) {
            try {
                impl.downloadMavenPath(path,dep);
                return
            } catch (Exception e) {
                onException(e);
                if (leftRetry <= 1) {
                    throw e;
                }
                leftRetry--;
            }
        }
    }

    @Override
    void downloadPathImplSpecific(String path, boolean dep) {
        int leftRetry = retryCount
        while (true) {
            try {
        impl.downloadPathImplSpecific(path,dep)
                return
            } catch (Exception e) {
                onException(e);
                if (leftRetry <= 1) {
                    throw e;
                }
                leftRetry--;
            }
        }

    }

    @Override
    List<MavenId> resolveAndDownloadDeepDependencies(MavenId mavenId, boolean downloadSource, boolean dep, IBiblioRepository repo) {
        return resolveAndDownloadDeepDependencies(mavenId,downloadSource,dep)
    }

    @Override
    List<MavenId> resolveAndDownloadDeepDependencies(MavenId mavenId, boolean downloadSource, boolean dep) {
        int leftRetry = retryCount
        while (true) {
            try {
                return impl.resolveAndDownloadDeepDependencies(mavenId, downloadSource, dep)
            } catch (Exception e) {
                onException(e);
                if (leftRetry <= 1) {
                    throw e;
                }
                leftRetry--;
            }
        }

    }

    @Override
    void downloadSource(MavenId mavenId) {
        int leftRetry = retryCount
        while (true) {
            try {
                impl.downloadSource(mavenId)
                return
            } catch (Exception e) {
                onException(e);
                if (leftRetry <= 1) {
                    throw e;
                }
                leftRetry--;
            }
        }
    }

    @Override
    void downloadSource(MavenId mavenId, IBiblioRepository repo) {
        downloadSource(mavenId)
    }

    @Override
    File getMavenLocalDir() {
        return impl.getMavenLocalDir();
    }

    @Override
    URL getMavenRepoUrl() {
        return impl.getMavenRepoUrl();
    }
}
