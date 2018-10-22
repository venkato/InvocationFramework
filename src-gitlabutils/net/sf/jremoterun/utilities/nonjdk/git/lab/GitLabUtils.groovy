package net.sf.jremoterun.utilities.nonjdk.git.lab

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiClient
import org.gitlab4j.api.Pager
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.ProjectFilter
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.logging.LoggingFeature

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class GitLabUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitLabApi gitLabClient;
    public List<String> ignoreRepos = [];
    public int maxProjectsOnPage = 30;
    public boolean cloneAsSsh = true;

    GitLabUtils(GitLabApi gitLabClient) {
        this.gitLabClient = gitLabClient
    }

    List<Project> getChildProjects1(String orgName, int maxProjectsOnPage1) {
        return getChildProjects(gitLabClient, orgName, maxProjectsOnPage1)
    }

    static List<Project> getChildProjects(GitLabApi gitLabClient1, String orgName, int maxProjectsOnPage1) {
        ProjectFilter pf = new ProjectFilter()
        pf.withSearchNamespaces(true);
        pf.withSearch(orgName);
        Pager<Project> projects = gitLabClient1.getProjectApi().getProjects(pf, maxProjectsOnPage1);
        int totalItems1 = projects.getTotalItems()
        if (totalItems1 > maxProjectsOnPage1) {
            throw new Exception("got too many projects : ${totalItems1}")
        }
        List<Project> next1 = projects.next()
        if (next1.size() <= 0) {
            throw new Exception("no repo found for ${orgName}")
        }
        return next1;
    }

    void cloneOrg(String orgName) {
        cloneOrg(orgName, maxProjectsOnPage)
    }

    void cloneOrg(String orgName, int maxProjectsOnPage1) {
        List<Project> current = getChildProjects1(orgName, maxProjectsOnPage1)
        current.each {
            try {
                if (canCloneRepo(it)) {
                    log.info "clonning ${it.getNameWithNamespace()}"
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

    boolean canCloneRepo(Project repo) {
        return !(ignoreRepos.contains(repo.getName()));
    }

    GitSpec createGitSpec(Project repository) {
        if (cloneAsSsh) {
            return new GitSpec(repository.getSshUrlToRepo())
        }
        return new GitSpec(repository.getHttpUrlToRepo())
    }

    void cloneRepo(Project repository) {
        GitSpec gitSpec = createGitSpec(repository);
        GitRepoUtils.updateGitRepo2(gitSpec);
    }


    static ClientConfig getClientConfig(GitLabApi gitLabApi) {
        GitLabApiClient gitLabApiClient = JrrClassUtils.getFieldValue(gitLabApi, 'apiClient') as GitLabApiClient
        ClientConfig clientConfig = JrrClassUtils.getFieldValue(gitLabApiClient, 'clientConfig') as ClientConfig
        return clientConfig
    }

    static void addLogging(GitLabApi gitLabApi) {
        ClientConfig clientConfig = getClientConfig(gitLabApi)
        LoggingFeature loggingFeature = new LoggingFeature(log, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 1000);
        clientConfig.register(loggingFeature)
    }

}



