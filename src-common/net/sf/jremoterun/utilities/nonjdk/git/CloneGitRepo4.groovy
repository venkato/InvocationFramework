package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.StringFindRange
import net.sf.jremoterun.utilities.nonjdk.StringUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
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

    public static GitCommandConfigure gitCommandConfigure

    static void runCustomize(GitCommand gitCommand){
        if(gitCommandConfigure!=null){
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

    File cloneGitRepo3(GitSpec src) {
        String dirSuffix = createGitRepoSuffix(src.repo)
        log.info "${dirSuffix}"
        File toDir3 = new File(gitBaseDir, dirSuffix+'/git')
        log.info "${toDir3}"
        cloneGitRepo4(src, toDir3);
        File checkFile = new File(toDir3,'.git')
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
                FileUtils.copyDirectory(tmpGitDir, toDir3)
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
        if(src.endsWith('.git')){
//            src.substring(0,)
            src = src.substring(0, src.length() - 4 )
//            StringFindRange findRange=new StringFindRange(src)
//            findRange.end =findRange.end-4
//            src = findRange.subStringInclusiveBoth()
            log.info "new git ref = ${src}"
        }
        return UrlSymbolsReplacer.replaceBadSymbols(src)
    }

    void cloneGitRepo2(File toDir, GitSpec gitRef) {
        cloneGitRepo(toDir,gitRef)
    }


    void custom( CloneCommand cloneCommand ){
        runCustomize(cloneCommand)
    }


    void cloneGitRepo(File toDir, GitSpec gitRef) {
        log.info "downloading ${gitRef.repo}"
        assert toDir.parentFile.exists()
        if (toDir.exists()) {
            assert toDir.listFiles().length == 0
        }
        CloneCommand cloneCommand = Git.cloneRepository()
        switch (gitRef) {
            case { gitRef.branch != null }:
                cloneCommand.branch = gitRef.branch
                break
            case { gitRef.commitId != null }:
            case { gitRef.tag != null }:
                throw new UnsupportedOperationException()
                break
            default:
                cloneCommand.branch = 'master'
        }
        cloneCommand.setURI(gitRef.repo)
        File gitDir = new File(toDir, ".git");
        cloneCommand.setGitDir(gitDir)
        cloneCommand.directory = toDir
        custom(cloneCommand)
        Git gitCloneResult = cloneCommand.call()
        log.info "${gitCloneResult}"
        assert gitDir.listFiles().length > 1
        assert toDir.listFiles().length > 0
        gitCloneResult.close()
        log.info("checkout fine : ${gitRef.repo}")
    }


}
