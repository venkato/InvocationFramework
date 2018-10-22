package net.sf.jremoterun.utilities.nonjdk.svn;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.git.SvnRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class SvnUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String svnDirPrefix = 'svn'

    SvnUtils2 svnUtils2 = new SvnUtils2()
    File svnRepo
    File gitTmpDir

    SvnUtils(File gitRepo, File gitTmpDir) {
        svnRepo = gitRepo.child(svnDirPrefix)
        svnRepo.mkdir()
        assert svnRepo.exists()

        this.svnRepo = svnRepo
        this.gitTmpDir = gitTmpDir
    }

    void checkoutSvnRefImpl(SvnSpec svnRef, File workingCopyDirectory) {
        String fullPath = svnRef.repo;
        if (svnRef instanceof SvnRef) {
            String pathInRepo = svnRef.branch
            if (StringUtils.isNotEmpty(pathInRepo)) {
                if (!pathInRepo.startsWith('/')) {
                    fullPath += '/';
                }
                fullPath += pathInRepo;
            }
        }
        if (svnRef.runExport) {
            svnUtils2.exportSvnRefImpl(fullPath, workingCopyDirectory)
        } else {
            svnUtils2.checkoutSvnRefImpl(fullPath, workingCopyDirectory)
        }

    }

    File getFileIfDownloaded(SvnSpec svnRef) {
        String checkoutDirSuffix = UrlSymbolsReplacer.replaceBadSymbols(svnRef.repo)
        File toDir3 = svnRepo.child(svnRef.repoPrefix + checkoutDirSuffix)
        return toDir3
    }

    File svnCheckout(SvnSpec svnRef) {
        File toDir3 = getFileIfDownloaded(svnRef)
        if (toDir3.exists()) {
            return toDir3
        }
        File tmpGitDir = findGitDownloadDir(svnDirPrefix + svnRef.repoPrefix)
        checkoutSvnRefImpl(svnRef, tmpGitDir)
        if (toDir3.exists()) {
            assert toDir3.deleteDir()
        }
        if (!tmpGitDir.renameTo(toDir3)) {
            log.info("can't rename ${tmpGitDir} to ${toDir3}, tring copy and delete")
            FileUtilsJrr.copyDirectory(tmpGitDir, toDir3)
            if (!FileUtils.deleteQuietly(tmpGitDir)) {
                log.info("failed delete ${tmpGitDir}")
            }
        }
        return toDir3

    }

    File findGitDownloadDir(String suffix) {
        int i = 10
        while (i < 100) {
            i++;
            File tmpGitDir = new File(gitTmpDir, suffix + "${i}")
            if (!tmpGitDir.exists()) {
                return tmpGitDir;
            }
        }
        throw new Exception("can't find free dir in ${gitTmpDir}")
    }


}
