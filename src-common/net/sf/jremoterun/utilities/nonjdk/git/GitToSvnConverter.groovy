package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.StringFindRange;

import java.util.logging.Logger;

@CompileStatic
class GitToSvnConverter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static String normalizeRepo(String repo) {
        String prefix = repo;
        if (prefix.endsWith('/')) {
            StringFindRange sr = new StringFindRange(prefix);
            sr.end = sr.end - 1;
            prefix = sr.subStringInclusiveBoth()
        }
        if (prefix.endsWith('.git')) {
            StringFindRange sr = new StringFindRange(prefix);
            sr.end = sr.end - 4;
            prefix = sr.subStringInclusiveBoth()
        }

        return prefix

    }

    // see https://docs.github.com/en/github/importing-your-projects-to-github/support-for-subversion-clients


    static SvnSpec buildRef2(GitSpec gitSpec) {
        return new SvnSpec(buildRef(gitSpec))
    }

    static String buildRef(GitSpec gitSpec) {
        String prefix = gitSpec.repo;
        if (!prefix.startsWith('http')) {
            throw new Exception('unsupported protocol : ' + prefix)
        }
        prefix = normalizeRepo(prefix);
        if (gitSpec.branch != null) {
            return prefix + '/branches/' + gitSpec.branch
        }
        if (gitSpec.tag != null) {
            return prefix + '/tags/' + gitSpec.tag
        }
        if (gitSpec.commitId != null) {
            throw new Exception('checkout by commit unsupported')
        }
        // if not add trunk, then will download all branches
        return prefix + '/trunk';
    }

}
