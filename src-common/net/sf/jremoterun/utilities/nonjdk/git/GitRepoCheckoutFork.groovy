package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.URIish
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils;

import java.util.logging.Logger

@CompileStatic
abstract class GitRepoCheckoutFork {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean trimBrancheNameSpaces = true

    public String myUserName = System.getProperty('user.name');

    public static String githubPrPrefix = 'pull'
    public static String  gitlabPrPrefix = 'merge-requests'



    GitRemoteFind orgRemoteFind = new GitRemoteFind() {
        @Override
        boolean isRepoGood(String uri3) {
            return isOrgRemote1(uri3)
        }
    };

    GitRemoteFind myRemoteFind = new GitRemoteFind() {
        @Override
        boolean isRepoGood(String uri3) {
            return isMyRemote1(uri3)
        }
    };

    File tmpDirBase;

    GitRepoCheckoutFork(File tmpDirBase) {
        this.tmpDirBase = tmpDirBase
    }


    void checkoutBranch(GitRepoUtils gitRepoUtils, String newBranch, String originalBranch) {
        checkoutBranch(gitRepoUtils, newBranch, originalBranch, true)
    }

    RefSpec createRefSpec(String src,String target){
        return new RefSpec("${src}:${target}");
    }

    void checkoutPrCommon(GitRepoUtils gitRepoUtils, String newBranch, int prId, String prefix){
        if (newBranch == null) {
            newBranch = gitRepoUtils.buildBranchName("pr_${prId}", new Date())
        }
        String orgRemote = orgRemoteFind.detectRemote(gitRepoUtils);
        String originalPath = "refs/remotes/${orgRemote}/${prefix}/${prId}"
        RefSpec refSpec = createRefSpec("refs/${prefix}/${prId}/head",originalPath)
        gitRepoUtils.fetch2(orgRemote,refSpec)
        checkoutCoreRaw(gitRepoUtils,newBranch,originalPath,true)
    }

    GitRepoUtils createGitRepoUtils(File repo){
        return new GitRepoUtils(repo);
    }

    @Deprecated
    void updatee(File repo, String newBranch, String originalBranch, boolean setMyRemote) {
        assert repo.exists()
        GitRepoUtils gitRepoUtils = createGitRepoUtils(repo);
        checkoutBranch(gitRepoUtils,newBranch,originalBranch,setMyRemote)
    }

    void checkoutBranch(GitRepoUtils gitRepoUtils, String newBranch, String originalBranch, boolean setMyRemote) {
        if (originalBranch == null) {
            originalBranch = 'master'
        }
        if(trimBrancheNameSpaces){
            originalBranch = originalBranch.trim()
        }
        if (newBranch == null) {
            newBranch = gitRepoUtils.buildBranchName(originalBranch, new Date())
        }

        String orgRemote = orgRemoteFind.detectRemote(gitRepoUtils);
        gitRepoUtils.fetch2(orgRemote,originalBranch);
        String originalBranchPath = "remotes/${orgRemote}/${originalBranch}"
        checkoutCoreRaw(gitRepoUtils,newBranch,originalBranchPath,setMyRemote)

//        URIish uri = gitRepoUtils.getRemoteUri(orgRemote)
//        File tmpDir = tmpDirBase.child(uri.getPath().replace('/', ''))
//        tmpDir.mkdir()
//        gitRepoUtils.checkoutSavedDirtyFiles2(originalBranchPath, tmpDir, newBranch)
//        if (setMyRemote) {
//            gitRepoUtils.setRemote(null, myRemote)
//        }
//        gitRepoUtils.gitRepository.getConfig().save()
    }

    void checkoutCoreRaw(GitRepoUtils gitRepoUtils, String newBranch, String originalPath, boolean setMyRemote) {
        if (newBranch == null) {
            throw new Exception("set branch name")
        }
        if(trimBrancheNameSpaces){
            newBranch = newBranch.trim()
            originalPath = originalPath.trim()
        }

        String orgRemote = orgRemoteFind.detectRemote(gitRepoUtils);
        String myRemote;
        if (setMyRemote) {
            Set<String> remoteNames1 = gitRepoUtils.gitRepository.getRemoteNames()
            if (remoteNames1.size() < 2) {
                // use to add new repo : git remote add nameOfRepo repoUrl
                throw new Exception("too low remotes : ${remoteNames1}")
            }
            myRemote = myRemoteFind.detectRemote(gitRepoUtils);
        }
        URIish uri = gitRepoUtils.getRemoteUri(orgRemote)
        File tmpDir = tmpDirBase.child(uri.getPath().replace('/', ''))
        tmpDir.mkdir()
        gitRepoUtils.checkoutSavedDirtyFiles2(originalPath, tmpDir, newBranch)
        if (setMyRemote) {
            gitRepoUtils.setRemote(null, myRemote)
        }
        gitRepoUtils.gitRepository.getConfig().save()
    }

    abstract boolean isOrgRemote1(String uri);

    boolean isMyRemote1(String uri) {
        return uri.startsWith(myUserName);
    }


}
