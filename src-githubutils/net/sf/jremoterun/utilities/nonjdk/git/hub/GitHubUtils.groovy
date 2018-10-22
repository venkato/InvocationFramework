package net.sf.jremoterun.utilities.nonjdk.git.hub

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.logging.Logger;

@CompileStatic
abstract class GitHubUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitHubClient gitHubClient;
    public RepositoryService rs;
    public List<String> ignoreRepos = [];

    GitHubUtils(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient
        // new GitHubClient(''host).setCred(user,pass)
        rs = new RepositoryService(gitHubClient)
    }

    void cloneOrg(String orgName) {
        List<Repository> repositories = rs.getOrgRepositories(orgName);
        log.info "repos size : ${repositories.size()}"
        if (repositories.size() <= 0) {
            throw new Exception("no repo found for ${orgName}")
        }

        repositories.each {
            try {
                if (canCloneRepo(it)) {
                    cloneRepo(it)
                } else {
                    log.info "ignore repo ${it.getName()}"
                }
            } catch (Exception e) {
                log.info "failed clone repo ${it.getName()} ${e}"
                throw e
            }
        }
    }

    boolean canCloneRepo(Repository repo) {
        return !(ignoreRepos.contains(repo.getName()));
    }

    //repository.getName()
    abstract GitSpec createGitSpec(Repository repository)

    void cloneRepo(Repository repository) {
        GitSpec gitSpec = createGitSpec(repository)
        GitRepoUtils.updateGitRepo2(gitSpec)
    }

}
