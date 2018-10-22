package net.sf.jremoterun.utilities.nonjdk.idea.github

import com.intellij.dvcs.repo.Repository
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryImpl
import git4idea.repo.GitRepositoryManager
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class IdeaGitgubAddRepo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void addRepoD(File gitRepoDir){
        addRepo(gitRepoDir,OSIntegrationIdea.getOpenedProject())
    }

    static void addRepo(File gitRepoDir,Project openedProject){
        VcsRepositoryManager vcsRepositoryManager = VcsRepositoryManager.getInstance(openedProject);
        VirtualFile  virtualFile = OSIntegrationIdea.conevertFileToVirtual(gitRepoDir)
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(openedProject)
        GitRepository gitRepository = gitRepositoryManager.getRepositoryForFile(virtualFile)
        if(gitRepository==null){
            Disposable disposable = Disposer.newDisposable()
            gitRepository = GitRepositoryImpl.createInstance(virtualFile,openedProject,disposable,true);
            gitRepositoryManager.addExternalRepository(virtualFile,gitRepository);
        }
        Map<VirtualFile, Repository> myRepos = JrrClassUtils.getFieldValue(vcsRepositoryManager,'myRepositories') as Map<VirtualFile, Repository>
        myRepos.put(virtualFile,gitRepository)
    }

}
