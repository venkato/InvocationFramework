package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.Sortable;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@Canonical
@CompileStatic
@Sortable
class GitRef extends GitSpec implements GitRefRef{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String pathInRepo;

    GitRef(GitSpec repo, String pathInRepo) {
        this.repo = repo.repo
        this.branch = repo.branch
        this.commitId = repo.commitId
        this.tag = repo.tag
        this.pathInRepo = pathInRepo
    }

    GitRef(String repo, String pathInRepo) {
        this.repo = repo
        this.pathInRepo = pathInRepo
    }

    @Override
    String toString() {
        return "${repo} ${pathInRepo}"
    }

    @Override
    GitSpec getSpecOnly() {
        GitSpec gitSpec = new GitSpec(repo,commitId,branch,tag)
        return gitSpec
    }

    @Override
    GitRef getRef() {
        return this
    }
}
