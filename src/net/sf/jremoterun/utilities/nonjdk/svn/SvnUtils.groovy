package net.sf.jremoterun.utilities.nonjdk.svn;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import org.apache.commons.io.FileUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class SvnUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    SvnUtils2 svnUtils2 = new SvnUtils2()
    File svnRepo
    File gitTmpDir

    SvnUtils(File gitRepo, File gitTmpDir) {
        svnRepo = gitRepo.child('svn')
        svnRepo.mkdir()
        assert svnRepo.exists()

        this.svnRepo = svnRepo
        this.gitTmpDir = gitTmpDir
    }

    void checkoutSvnRefImpl(SvnSpec svnRef, File workingCopyDirectory) {
        svnUtils2.checkoutSvnRefImpl(svnRef.repo,workingCopyDirectory)
    }

    File svnCheckout(SvnSpec svnRef) {
        String checkoutDirSuffix = UrlSymbolsReplacer.replaceBadSymbols(svnRef.repo)
        File toDir3 = svnRepo.child(checkoutDirSuffix)
        if (toDir3.exists()) {
            return toDir3
        }
        File tmpGitDir = findGitDownloadDir("svn")
        checkoutSvnRefImpl(svnRef, tmpGitDir)
        if (toDir3.exists()) {
            assert toDir3.deleteDir()
        }
        if (!tmpGitDir.renameTo(toDir3)) {
            log.info("can't rename ${tmpGitDir} to ${toDir3}, tring copy and delete")
            FileUtils.copyDirectory(tmpGitDir, toDir3)
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
