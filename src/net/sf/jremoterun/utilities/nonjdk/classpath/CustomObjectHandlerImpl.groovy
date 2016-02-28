package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3
import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo2
import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo4
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.SvnRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SourceForgeDownloader
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import net.sf.jremoterun.utilities.nonjdk.svn.SvnUtils
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
class CustomObjectHandlerImpl implements CustomObjectHandler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File replicaDir

    CloneGitRepo4 cloneGitRepo3

    MavenCommonUtils mcu = new MavenCommonUtils();

    SourceForgeDownloader sfDownloader

    UrlDownloadUtils3 urlDownloadUtils = new UrlDownloadUtils3()

    File gitTmpDir

    SvnUtils svnUtils


    CustomObjectHandlerImpl(File gitRepo) {
        cloneGitRepo3 = new CloneGitRepo4(gitRepo);
        replicaDir = new File(gitRepo, 'replica')
        replicaDir.mkdir()
        assert replicaDir.exists()
        File sfDir = gitRepo.child('sf')
        sfDir.mkdir()
        assert sfDir.exists()
        sfDownloader = new SourceForgeDownloader(sfDir)
        this.gitTmpDir = new File(gitRepo, "tmp")
        gitTmpDir.mkdir()
        svnUtils = new SvnUtils(gitRepo, gitTmpDir)
    }

    @Override
    File resolveToFile(Object object) {
        switch (object) {
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                return resolveToFile(refLink.enumm)
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect!=null
                return resolveToFile(redirect)
            case { object instanceof SfLink }:
                SfLink sfLink = object as SfLink
                return sfDownloader.download(sfLink)
            case { object instanceof GitRefRef }:
                GitRefRef gitRef = (GitRefRef) object;
                return resolveRef(gitRef.ref)
            case { object instanceof GitSpec }:
                GitSpec gitSpec = (GitSpec) object;
                return cloneGitRepo3.cloneGitRepo3(gitSpec)
            case { object instanceof SvnSpec }:
                SvnSpec svnRef = object as SvnSpec
                return resolveSvnRef(svnRef)
            case { object instanceof UrlProvided }:
                UrlProvided u = object as UrlProvided
                URL url = u.convertToUrl()
                return urlDownloadUtils.downloadUrl(url)
            default:
                throw new IllegalArgumentException("${object.class.name} ${object}")
        }

    }

    @Override
    void add(AddFilesToClassLoaderCommon adder, Object object) {
        switch (object) {
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                adder.addGenericEntery(refLink.enumm)
//                add(adder, refLink.enumm)
                break
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect!=null
                add(adder,redirect)
                break
            case { object instanceof GitBinaryAndSourceRefRef }:
                GitBinaryAndSourceRefRef gitRef = (GitBinaryAndSourceRefRef) object;
                addGitRef(adder, gitRef.ref)
                break;
            default:
                File f = resolveToFile(object)
                adder.add f
                break;
        }
    }


    File resolveSvnRef(SvnSpec svnRef) {
        File checkout = svnUtils.svnCheckout(svnRef)
        if (!(svnRef instanceof SvnRef)) {
            return checkout
        }
        File fileInRepo = checkout.child(svnRef.pathInRepo)
        if (!fileInRepo.exists()) {
            throw new FileNotFoundException("failed find ${svnRef}")
        }
        fileInRepo = getRef23(fileInRepo)
        return fileInRepo
    }

    public boolean updateRepoIfRefNotExists = true

    void addGitRef(AddFilesToClassLoaderCommon adder, GitBinaryAndSourceRef gitRef) {
        File gitRepo = cloneGitRepo3.cloneGitRepo3(gitRef)
        File bin = new File(gitRepo, gitRef.pathInRepo)
        File src = new File(gitRepo, gitRef.src)
        boolean allFound = bin.exists() && src.exists()
        if (!allFound && updateRepoIfRefNotExists) {
            log.info "updating repo : ${gitRef}"
            CloneGitRepo2.updateGitRepo2(gitRef)
        }
        if (!bin.exists()) {
            throw new FileNotFoundException("bin not found : ${gitRef}")
        }
        if (!src.exists()) {
            throw new FileNotFoundException("src not found : ${gitRef}")
        }
        bin = getRef23(bin)
        src = getRef23(src)
        BinaryWithSource binaryWithSource = new BinaryWithSource(bin, src)
        adder.addBinaryWithSource(binaryWithSource)
    }

    File resolveRef(GitRef gitRef) {
        File gitRepo = cloneGitRepo3.cloneGitRepo3(gitRef)
        File fileInRepo = new File(gitRepo, gitRef.pathInRepo)
        if (!fileInRepo.exists()) {
            log.info "file not exist : ${fileInRepo} , ${gitRef}"
            return fileInRepo
        }
        fileInRepo = getRef23(fileInRepo)
        return fileInRepo
    }

    private File getRef23(File fileInRepo) {
        if (fileInRepo.isFile()) {
            String pathToParent = mcu.getPathToParent(cloneGitRepo3.gitBaseDir, fileInRepo)
            File f = new File(replicaDir, pathToParent)
            copyFileIfNeeded(fileInRepo, f)
            return f;
        }
        return fileInRepo
    }

    static boolean isCopyFileNeeded(File src, File dest) {
        assert src.exists()
        dest.parentFile.mkdirs()
        assert dest.parentFile.exists()
        if (!dest.exists()) {
            return true
        }
        if (src.length() != dest.length()) {
            return true
        }
        if (src.lastModified() != dest.lastModified()) {
            return true
        }
        return false
    }

    static void copyFileIfNeeded(File src, File dest) {
        boolean needCopy = isCopyFileNeeded(src, dest)
        if (needCopy) {
            log.info("coping ${src} to ${dest}")
            FileUtils.copyFile(src, dest)
            dest.setLastModified(src.lastModified())
        }
    }

    void addGitRef(AddFilesToClassLoaderCommon adder, GitRef gitRef) {
        File fileInRepo = resolveRef(gitRef)
        adder.addF(fileInRepo)
    }
}
