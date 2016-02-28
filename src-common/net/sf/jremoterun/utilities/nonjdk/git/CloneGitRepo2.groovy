package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish

import java.util.logging.Logger

@CompileStatic
class CloneGitRepo2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String gitNative = "git"

    static void addAllFiles(File dir) {
        assert dir.exists()
        GeneralUtils.runNativeProcess("${gitNative} add -A", dir, true)
        log.info("finished git add")
    }


    static void gitCommit(File dir) {
        String commit2 = "${gitNative} commit -m '${new Date().format('yyyy-MM-dd--HH-mm')}'"
        try {
            GeneralUtils.runNativeProcess(commit2, dir, true)
        } catch (Exception e) {
            log.info "${e}"
        }
    }

    static void updateGitRepo2(GitSpec gitSpec) {
        File f = gitSpec.specOnly.resolveToFile()
        String branch = gitSpec.branch
        if (branch == null) {
            branch = 'master'
        }
        updateGitRepo3(f, branch);
    }

    static URIish getRemoteUri(Repository repository, String remoteName) {
        if (remoteName == null) {
            remoteName = org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
        }
        RemoteConfig remoteConfig = new RemoteConfig(repository.getConfig(), remoteName)
        List<URIish> uRIs = remoteConfig.getURIs()
        int size = uRIs.size()
        switch (size) {
            case 0:
                throw new IllegalStateException("strange remote : ${remoteName}")
            case 1:
                return uRIs[0];
            default:
                throw new IllegalStateException("Found ${size} remotes for ${remoteName} : ${uRIs}")
        }

    }


    static void fetch(File dir) {
        Git git = Git.open(dir)
        FetchCommand fetch = git.fetch()
        CloneGitRepo4.runCustomize(fetch)
        FetchResult fetchResult = fetch.call()
        log.info("${fetchResult}")
    }

    static void updateGitRepo3(File dir, String branch) {
        fetch(dir)
        updateGitRepo2(dir, branch)
    }

    static void updateGitRepo2(File dir, String branch) {
        Git git = Git.open(dir)
        try {
            StatusCommand statusCommand = git.status()
            CloneGitRepo4.runCustomize(statusCommand)
            Status statusResult = statusCommand.call()
            log.info "${statusResult}"
            log.info "Has uncommited : ${statusResult.hasUncommittedChanges()}"
            log.info "Is clean : ${statusResult.isClean()}"
            String gitStatus3 = "${gitNative} status"
            GeneralUtils.runNativeProcess(gitStatus3, dir, true)

            log.info "conflict : ${statusResult.conflicting}"
            log.info "modified : ${statusResult.modified}"
            log.info "untacked : ${statusResult.untrackedFolders}"
            log.info "missing : ${statusResult.missing}"
            addAllFiles(dir)
            gitCommit(dir)

            String branchName = "${branch}-${new Date().format("yyyy-MM-dd-HH-mm-ss")}"

            String cmd3 = "${gitNative} checkout -b ${branchName} origin/${branch}"
            GeneralUtils.runNativeProcess(cmd3, dir, true)
        } finally {
            git.close()

        }
    }


}
