package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolverException
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import org.apache.commons.io.FileUtils

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class AddFilesToClassLoaderGroovyDownloader extends AddFilesToClassLoaderGroovy {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AddFilesToClassLoaderGroovyDownloader() {
    }

    @Override
    void addFileImpl(File file) throws Exception {

    }

    @Override
    void addM(MavenId artifact) throws IOException {
        try {
            super.addM(artifact)
        } catch (Exception e) {
            Throwable rootException = JrrUtils.getRootException(e);
            if (rootException instanceof IvyDepResolverException) {
                log.info"failed download : ${artifact}: ${rootException}"
            } else {
                log.log(Level.WARNING, "failed download : ${artifact}", rootException)
            }
        }
    }


    void addM1(MavenId artifact) throws IOException {
        addM(artifact)
    }

    void addWithDeps(MavenId artifact) throws IOException {
            addMWithDependeciesDownload(artifact)
    }

    @Override
    void addMWithDependeciesDownload(MavenId artifact) throws IOException {
        try {
            super.addMWithDependeciesDownload(artifact)
        } catch (Exception e) {
            Throwable rootException = JrrUtils.getRootException(e);
            if (rootException instanceof IvyDepResolverException) {
                log.info"failed download : ${artifact}: ${rootException}"
            } else {
                log.log(Level.WARNING, "failed download : ${artifact}", rootException)
            }
        }
    }

    void saveFilesToFolder(File groovyFile, File toFolder) {
        JrrUtilities.checkFileExist(toFolder)
        addFromGroovyFile(groovyFile)
        addedFiles2.findAll { it != null }.each {
            JrrUtilities.checkFileExist(it)
            if (it.file) {
                FileUtilsJrr.copyFileToDirectory(it, toFolder)
            } else {
                File toFolder2 = new File(toFolder, "classes")
                toFolder2.mkdir()
                assert toFolder2.exists()
                FileUtilsJrr.copyDirectory(it, toFolder2)
            }
        }
    }


    void downloadWithSources(MavenId mavenId) {
        MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver.resolveAndDownloadDeepDependencies(mavenId, true, true)
    }
//
//    void downloadNativeArtifact(DefaultArtifact groupArtifactVersion) {
//        defaultMavenDepDownloader.classLoaderBuilder.download(groupArtifactVersion, false, false);
//    }


}
