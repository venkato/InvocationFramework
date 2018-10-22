package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.JavaVersionChecker
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddToAdderSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3

import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo4
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpecRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SourceForgeDownloader
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import net.sf.jremoterun.utilities.nonjdk.svn.SvnUtils
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipFile

@CompileStatic
class CustomObjectHandlerImpl implements CustomObjectHandler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File replicaDir

    CloneGitRepo4 cloneGitRepo3

    MavenCommonUtils mcu = new MavenCommonUtils();

    SourceForgeDownloader sfDownloader

    UrlDownloadUtils3 urlDownloadUtils = new UrlDownloadUtils3()

    File gitTmpDir;
    File androidUnArchive;

    SvnUtils svnUtils;

    static CustomObjectHandlerImpl getSelfRef(){
        MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl;
    }

    CustomObjectHandlerImpl(File gitRepo) {
        JavaVersionChecker.checkJavaVersion();
        cloneGitRepo3 = new CloneGitRepo4(gitRepo);
        replicaDir = new File(gitRepo, 'replica')
        replicaDir.mkdir()
        assert replicaDir.exists()
        File sfDir = gitRepo.child('sf')
        sfDir.mkdir()
        assert sfDir.exists()
        sfDownloader = new SourceForgeDownloader(sfDir)
        this.gitTmpDir = new File(gitRepo, "tmp")
        this.androidUnArchive = new File(gitRepo, "android")
        gitTmpDir.mkdir()
        svnUtils = new SvnUtils(gitRepo, gitTmpDir)
    }

    @Override
    File resolveToFileIfDownloaded(Object object) {
        switch (object) {
//            case { object instanceof RefLink }:
//                RefLink refLink = (RefLink) object
//                return resolveToFile(refLink.enumm)
//            case { object instanceof ToFileRefRedirect }:
//                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
//                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
//                assert redirect != null
//                return resolveToFile(redirect)
            case { object instanceof ZeroOverheadFileRef }:
                ZeroOverheadFileRef fileToFileRef = object as ZeroOverheadFileRef;
                return fileToFileRef.resolveToFile();
            case { object instanceof UnzipRef }:
                return resolveUnzipIfDownloaded(object as UnzipRef)
            case { object instanceof FileChildLazyRef }:
                FileChildLazyRef refLink2 = (FileChildLazyRef) object
                return resolveLazyChildOfDownloaded(refLink2)
            case { object instanceof SfLink }:
                SfLink sfLink = object as SfLink
                return sfDownloader.resolveIfDownloaded(sfLink)
            case { object instanceof GitRefRef }:
                GitRefRef gitRef = (GitRefRef) object;
                return resolveRefIfDownloaded(gitRef.ref)
            case { object instanceof GitSpecRef }:
                GitSpecRef gitSpec = (GitSpecRef) object;
                return cloneGitRepo3.getFileIfDownloaded(gitSpec.getGitSpec())
            case { object instanceof UrlProvided }:
                return resolveToFileIfDownloaded(((UrlProvided) object).convertToUrl())
            case { object instanceof IfFrameworkResourceDirs }:
                IfFrameworkResourceDirs resourceDirs = object as IfFrameworkResourceDirs
                if(InfocationFrameworkStructure.ifDir!=null){
                    return resourceDirs.resolveToFile()
                }
                log.info("need define InfocationFrameworkStructure.ifDir to resolve : ${object}")
                return null;
            case { object instanceof IfFrameworkSrcDirs }:
                IfFrameworkSrcDirs resourceDirs = object as IfFrameworkSrcDirs
                if(InfocationFrameworkStructure.ifDir!=null){
                    return resourceDirs.resolveToFile()
                }
                log.info("need define InfocationFrameworkStructure.ifDir to resolve : ${object}")
                return null;
            case { object instanceof SvnSpecRef }:
                SvnSpecRef svnRef = object as  SvnSpecRef
                return getSvnRefIfDownloaded(svnRef.getSvnSpec())
//            case { object instanceof AndroidArchive }:
//                AndroidArchive aar = (AndroidArchive) object;
//                return downloadAndroid(aar);
//            case { object instanceof UrlProvided }:
//                UrlProvided u = object as UrlProvided
//                URL url = u.convertToUrl()
//                return urlDownloadUtils.downloadUrl(url)
//            case { object instanceof MavenIdContains }:
//                MavenIdContains u = object as MavenIdContains
//                MavenId mavenId = u.getM()
//                return mavenId.resolveToFile()
//            case { object instanceof ToFileRef2 }:
//                ToFileRef2 u = object as ToFileRef2
//                return u.resolveToFile()
            default:
                log.info("not supported : ${object.class.name} ${object}")
                return null
        }
    }

    @Override
    File resolveToFile(Object object) {
        switch (object) {
            case { object instanceof FileChildLazyRef }:
                FileChildLazyRef refLink2 = (FileChildLazyRef) object
                return resolveLazyChild(refLink2)
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                return resolveToFile(refLink.enumm)
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                return resolveToFile(redirect)
            case { object instanceof UnzipRef }:
                return downloadAndResolveUnzip(object as UnzipRef)
            case { object instanceof SfLink }:
                SfLink sfLink = object as SfLink
                return sfDownloader.download(sfLink)
            case { object instanceof GitRefRef }:
                GitRefRef gitRef = (GitRefRef) object;
                return resolveRef(gitRef.ref)
            case { object instanceof GitSpecRef }:
                GitSpecRef gitSpec = (GitSpecRef) object;
                return cloneGitRepo3.cloneGitRepo3(gitSpec.getGitSpec())
            case { object instanceof SvnSpec }:
                SvnSpec svnRef = object as SvnSpec
                return resolveSvnRef(svnRef)
            case { object instanceof AndroidArchive }:
                AndroidArchive aar = (AndroidArchive) object;
                return downloadAndroid(aar);
            case { object instanceof UrlProvided }:
                UrlProvided u = object as UrlProvided
                URL url = u.convertToUrl()
                return urlDownloadUtils.downloadUrl(url)
            case { object instanceof MavenIdAndRepoContains }:
                MavenIdAndRepoContains u2 = object as MavenIdAndRepoContains
                MavenIdAndRepo mavenId2 = u2.getMavenIdAndRepo()
                return mavenId2.resolveToFile()

            case { object instanceof MavenIdContains }:
                MavenIdContains u = object as MavenIdContains
                MavenId mavenId = u.getM()
                return mavenId.resolveToFile()
            case { object instanceof ToFileRef2 }:
                ToFileRef2 u = object as ToFileRef2
                return u.resolveToFile()
            default:
                throw new IllegalArgumentException("${object.class.name} ${object}")

        }

    }

    String resolvePathForRef(ToFileRef2 ref) {
        if (ref == null) {
            throw new NullPointerException('ref is null')
        }
        if (ref instanceof UrlProvided) {
            return urlDownloadUtils.mcu.buildDownloadUrlSuffix(ref.convertToUrl())
        }
        if (ref instanceof SvnSpec) {
            return UrlSymbolsReplacer.replaceBadSymbols(ref.repo)
        }
        if (ref instanceof GitSpec) {
            return CloneGitRepo4.createGitRepoSuffix(ref.repo);
        }
        if (ref instanceof FileChildLazyRef) {
            String parent = resolvePathForRef(ref.parentRef)
            return parent + '/' + ref.child;
        }
        if (ref instanceof MavenIdAndRepoContains) {
            return resolvePathForRef(ref.getMavenIdAndRepo().m)
        }
        if (ref instanceof MavenIdContains) {
            MavenId mavenId = ref.m
            String childd = mavenId.toString()
            childd = childd.replace(':', '/')
            return "maven/${childd}"
        }
        throw new UnsupportedOperationException("${ref.getClass().getName()} ${ref}")
    }

    File resolveUnzipIfDownloaded(UnzipRef ref) {
        File zipFile = resolveToFileIfDownloaded(ref.refToZip)
        if (zipFile == null) {
            return null
        }
        String suffix = resolvePathForRef(ref.refToZip);
        File f = urlDownloadUtils.unzipDir.child(suffix)
        if (!f.exists()) {
            log.info "file not exist : ${f}"
            return null
        }
        return f
    }

    File downloadAndResolveUnzip(UnzipRef ref) {
        File zipFile = resolveToFile(ref.refToZip)
        String suffix = resolvePathForRef(ref.refToZip);
        return urlDownloadUtils.unzip(zipFile, suffix)
    }

    File resolveLazyChild(FileChildLazyRef childLazyRef) {
        File parentRefResolved = childLazyRef.parentRef.resolveToFile()
        if (parentRefResolved == null) {
            throw new IOException("Failed resolve : ${childLazyRef.parentRef}")
        }
        if (!parentRefResolved.exists()) {
            throw new FileNotFoundException(parentRefResolved.getAbsolutePath())
        }
        File childFile = parentRefResolved.child(childLazyRef.child)
        if (!childFile.exists()) {
            throw new FileNotFoundException(childFile.getAbsolutePath())
        }
        return childFile
    }


    File resolveLazyChildOfDownloaded(FileChildLazyRef childLazyRef) {
        File parentRefResolved = resolveToFileIfDownloaded(childLazyRef.parentRef);
        if (parentRefResolved == null) {
            return null
        }
        if (!parentRefResolved.exists()) {
            return null
        }
        File childFile = parentRefResolved.child(childLazyRef.child)
        if (!childFile.exists()) {
            throw new FileNotFoundException(childFile.getAbsolutePath())
        }
        return childFile
    }

    File downloadAndroid(AndroidArchive androidArchive) {
        File someDir = new File(mcu.mavenDefaultSettings.grapeLocalDir, "${androidArchive.m.groupId}/${androidArchive.m.artifactId}")

        File aarFile = new File(someDir, "aars/${androidArchive.m.artifactId}-${androidArchive.m.version}.aar")
        if (!aarFile.exists()) {

            MavenDependenciesResolver ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(androidArchive.m, false, false)
            log.info "${dependencies}"
            if (!aarFile.exists()) {
                throw new FileNotFoundException("Failed resolve ${androidArchive} ${aarFile}")
            }
        }
        File toFile = new File(androidUnArchive, "${androidArchive.m.groupId}/${androidArchive.m.artifactId}/${androidArchive.m.artifactId}-${androidArchive.m.version}.jar")
        if (toFile.exists()) {
            return toFile
        }
        File parentFile = toFile.getParentFile()
        parentFile.mkdirs()
        if (!parentFile.exists()) {
            throw new IOException("Failed create : ${parentFile}")
        }
        boolean unpacked = ZipUtil.unpackEntry(new ZipFile(aarFile), "classes.jar", toFile)
        if (!unpacked) {
            throw new IOException("Failed find classes.jar in  : ${aarFile}")
        }
        if (!toFile.exists()) {
            throw new IOException("Failed extract classes.jar from  : ${aarFile}")
        }
        return toFile
    }

    @Override
    void add(AddFilesToClassLoaderCommon adder, Object object) {
        switch (object) {
            case { object instanceof AddToAdderSelf }:
                AddToAdderSelf addToAdderSelf = object as AddToAdderSelf;
                addToAdderSelf.addToAdder(adder)
                break
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                adder.addGenericEntery(refLink.enumm)
//                add(adder, refLink.enumm)
                break
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                add(adder, redirect)
                break
            case { object instanceof GitBinaryAndSourceRefRef }:
                GitBinaryAndSourceRefRef gitRef = (GitBinaryAndSourceRefRef) object;
                addGitRef(adder, gitRef.ref)
                break;
            case { object instanceof AndroidArchive }:
                AndroidArchive aar = (AndroidArchive) object;
                File f = downloadAndroid(aar);
                adder.add f
                if (adder instanceof AddFilesWithSourcesI) {
                    File sourceFile = adder.addSourceMNoExceptionOnMissing aar.m;
                    if (sourceFile == null) {
                        log.info "not source for android artifact : ${aar.m}"
                    }
                }
                break
            default:
                File f = resolveToFile(object)
                adder.add f
                break;
        }
    }


    File getSvnRefIfDownloaded(SvnSpec svnRef) {
        File checkout = svnUtils.getFileIfDownloaded(svnRef)
//        if (!(svnRef instanceof SvnRef)) {
        return checkout
//        }
//        if (checkout == null || !checkout.exists()) {
//            return null
//        }
//        File fileInRepo = checkout.child(svnRef.pathInRepo)
//        if (!fileInRepo.exists()) {
//            throw new FileNotFoundException("failed find ${svnRef}")
//        }
//        return fileInRepo
    }

    File resolveSvnRef(SvnSpec svnRef) {
        File checkout = svnUtils.svnCheckout(svnRef)
//        if (!(svnRef instanceof SvnRef)) {
            return checkout
//        }
//        File fileInRepo = checkout.child(svnRef.pathInRepo)
//        if (!fileInRepo.exists()) {
//            throw new FileNotFoundException("failed find ${svnRef}")
//        }
//        fileInRepo = getRef23(fileInRepo)
//        return fileInRepo
    }

    public boolean updateRepoIfRefNotExists = true

    void addGitRef(AddFilesToClassLoaderCommon adder, GitBinaryAndSourceRef gitRef) {
        File gitRepo = cloneGitRepo3.cloneGitRepo3(gitRef)
        File bin = new File(gitRepo, gitRef.pathInRepo)
        File src = new File(gitRepo, gitRef.src)
        boolean allFound = bin.exists() && src.exists()
        if (!allFound && updateRepoIfRefNotExists) {
            log.info "updating repo : ${gitRef}"
            GitRepoUtils.updateGitRepo2(gitRef)
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

    File resolveRefIfDownloaded(GitRef gitRef) {
        File gitRepo = cloneGitRepo3.getFileIfDownloaded(gitRef)
        if (gitRepo == null || !gitRepo.exists()) {
            return null;
        }

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
            FileUtilsJrr.copyFile(src, dest)
            dest.setLastModified(src.lastModified())
        }
    }

    void addGitRef(AddFilesToClassLoaderCommon adder, GitRef gitRef) {
        File fileInRepo = resolveRef(gitRef)
        adder.addF(fileInRepo)
    }
}
