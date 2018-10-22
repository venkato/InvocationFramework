package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrIdeaGenerator
import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo4
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

@CompileStatic
class GitCheckoutConsole implements ClassNameSynonym {


    File logfile

    private static boolean initdone = false


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CloneGitRepo4 cloneGitRepo3

    GitCheckoutConsole() {
        this(JrrIdeaGenerator.getGitRepo())
    }


    GitCheckoutConsole(File gitRepo) {
        cloneGitRepo3 = new CloneGitRepo4(gitRepo)
//        FirstDownloadSettings.gitRepoDir = gitRepo
//        FirstDownloadSettings.tmpDir = new File(gitRepo, "tmp/")
        logfile = new File(gitRepo, "logs/git.log")
        logfile.parentFile.mkdir()
    }


    void doinit() {
        if (!initdone) {
            initdone = true
            FileUtils.deleteQuietly(cloneGitRepo3.gitTmpDir)
            cloneGitRepo3.gitTmpDir.mkdir()
            if (ConsoleRedirect.outputFile == null) {
                ConsoleRedirect.setOutputWithRotationAndFormatter(logfile, 30);
            }
        }

    }

    File clone(String repo) {
        return clone3(repo, null, false)
    }


    File clone3(String repo, String branch, boolean forcePull) {
        doinit()
        if (repo.endsWith(".git")) {
            repo = repo.substring(0, repo.length() - 4)
//            repo = "${repo}.git"
        }
        String dirSuffix = CloneGitRepo4.createGitRepoSuffix(repo) + '/'+ GitSpec.checkoutDirDefault
        File toDir3 = new File(cloneGitRepo3.gitBaseDir, dirSuffix)
        GitSpec gitSpec = new GitSpec()
        gitSpec.repo = repo
        gitSpec.branch = branch
        if (forcePull && toDir3.exists()) {
            GitRepoUtils gitRepoUtils = new GitRepoUtils(toDir3)
            gitRepoUtils.pull()
        } else {
            cloneGitRepo3.cloneGitRepo4(gitSpec, toDir3)
            log.info "clone done : ${toDir3}"
        }
        return toDir3
    }

    File clone2(GitRef repo) {
        File toDir3 = clone(repo.repo)
        File path = new File(toDir3, repo.pathInRepo);
        if (!path.exists()) {
            throw new FileNotFoundException("failed find ${repo.pathInRepo} in ${toDir3}")
        }
        return path
    }

}
