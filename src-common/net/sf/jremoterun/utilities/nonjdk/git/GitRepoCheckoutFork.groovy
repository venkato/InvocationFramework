package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.lang.SystemUtils
import org.eclipse.jgit.transport.URIish

import java.util.logging.Logger;

@CompileStatic
abstract class GitRepoCheckoutFork {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String myUserName = System.getProperty('user.name');

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


    void updatee(File repo, String newBranch, String originalBranch) {
        updatee(repo, newBranch, originalBranch, true)
    }

    void updatee(File repo, String newBranch, String originalBranch, boolean setMyRemote) {
        if (originalBranch == null) {
            originalBranch = 'master'
        }
        assert repo.exists()
        GitRepoUtils gitRepoUtils = new GitRepoUtils(repo);
        if (newBranch == null) {
            newBranch = gitRepoUtils.buildBranchName(originalBranch, new Date())
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
        gitRepoUtils.fetch(orgRemote);
        URIish uri = gitRepoUtils.getRemoteUri(orgRemote)
        File tmpDir = tmpDirBase.child(uri.getPath().replace('/', ''))
        tmpDir.mkdir()
        gitRepoUtils.checkoutSavedDirtyFiles(originalBranch, tmpDir, orgRemote, newBranch)
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
