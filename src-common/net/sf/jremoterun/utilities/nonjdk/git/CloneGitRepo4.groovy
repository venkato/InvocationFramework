package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.GitCommand

import java.util.logging.Logger

@CompileStatic
class CloneGitRepo4 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static String gitDirSuffix = "git_download_"
    private static String fileSuffix = "file_"

    File gitBaseDir
    File gitTmpDir

    public static GitCommandConfigure gitCommandConfigure;
    public GitProgressMonitorJrr progressMonitor = new GitProgressMonitorJrr();

    static void runCustomize(GitCommand gitCommand) {
        if (gitCommandConfigure != null) {
            gitCommandConfigure.configure(gitCommand)
        }
    }

    CloneGitRepo4(File gitBaseDir2) {
        this.gitBaseDir = gitBaseDir2
        assert gitBaseDir.exists()
        this.gitTmpDir = new File(gitBaseDir, "tmp")
        gitTmpDir.mkdir()
        assert gitTmpDir.exists()
    }

    File getFileIfDownloaded(GitSpec src) {
        String dirSuffix = createGitRepoSuffix(src.repo)
        log.info "${dirSuffix}"
        File toDir3 = new File(gitBaseDir, dirSuffix + '/'+src.checkoutDir)
        log.info "${toDir3}"
        return toDir3
    }

    File cloneGitRepo3(GitSpec src) {
        File toDir3 = getFileIfDownloaded(src)

        cloneGitRepo4(src, toDir3);
        File checkFile = new File(toDir3, '.git')
        assert checkFile.exists()
        return toDir3;
    }

    void cloneGitRepo4(GitSpec src, File toDir3) {
        if (toDir3.exists() && toDir3.listFiles().length > 0) {
            log.info("already downloaded ${src}")
        } else {
            log.info "cloning ${src.repo} ..."
            File tmpGitDir = findGitDownloadDir();
            tmpGitDir.mkdir()
            cloneGitRepo2(tmpGitDir, src)
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
        }
    }

    File findGitDownloadDir() {
        int i = 10
        while (i < 100) {
            i++;
            File tmpGitDir = new File(gitTmpDir, gitDirSuffix + "${i}")
            if (!tmpGitDir.exists()) {
                return tmpGitDir;
            }
        }
        throw new Exception("can't find free dir in ${gitTmpDir}")
    }

    static void cleanDir(File toDir) {
        if (toDir.exists()) {
            toDir.deleteDir()
        }
        toDir.mkdirs()
        assert toDir.exists()
        assert toDir.listFiles().length == 0

    }

    static String createGitRepoSuffix(String src) {
        src =  GitToSvnConverter.normalizeRepo(src);
//        if (src.endsWith('.git')) {
////            src.substring(0,)
//            src = src.substring(0, src.length() - 4)
////            StringFindRange findRange=new StringFindRange(src)
////            findRange.end =findRange.end-4
////            src = findRange.subStringInclusiveBoth()
//            log.info "new git ref = ${src}"
//        }
        return UrlSymbolsReplacer.replaceBadSymbols(src)
    }

    void cloneGitRepo2(File toDir, GitSpec gitRef) {
        cloneGitRepo(toDir, gitRef)
    }


    void custom(CloneCommand cloneCommand) {
        runCustomize(cloneCommand)
    }


    void cloneGitRepo(File toDir, GitSpec gitRef) {
        log.info "downloading ${gitRef.repo}"
        assert toDir.parentFile.exists()
        if (toDir.exists()) {
            assert toDir.listFiles().length == 0
        }
        final CloneCommand cloneCommand1 = Git.cloneRepository()
        cloneCommand1.setProgressMonitor(progressMonitor)
        switch (gitRef) {
            case { gitRef.branch != null }:
                cloneCommand1.setBranch(gitRef.branch)
                break
            case { gitRef.commitId != null }:
            case { gitRef.tag != null }:
                throw new UnsupportedOperationException(gitRef.toString())
                break
            default:
                //cloneCommand1.branch = 'master'
                break
        }
        cloneCommand1.setURI(gitRef.repo)
        File gitDir = new File(toDir, ".git");
        cloneCommand1.setGitDir(gitDir)
        cloneCommand1.directory = toDir
        custom(cloneCommand1)
        Git gitCloneResult = cloneCommand1.call()
        log.info "${gitCloneResult}"
        assert gitDir.listFiles().length > 1
        assert toDir.listFiles().length > 0
        gitCloneResult.close()
        log.info("checkout fine : ${gitRef.repo}")
    }


}
