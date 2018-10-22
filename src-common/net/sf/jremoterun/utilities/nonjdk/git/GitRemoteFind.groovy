package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.transport.URIish;

import java.util.logging.Logger;

@CompileStatic
abstract class GitRemoteFind {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String detectRemote(GitRepoUtils gitRepoUtils){
        Set<String> remoteNames = gitRepoUtils.gitRepository.getRemoteNames()
        Set<String> reposss = remoteNames.findAll {
            URIish uri = gitRepoUtils.getRemoteUri(it)
            if(uri==null){
                log.info "uri is null for ${it}"
                return false
            }
            return isRepoGood(uri)
        }
        int size = reposss.size()
        switch (size){
            case 0:
                throw new Exception("not repo found from ${remoteNames}")
            case 1:
                return reposss.iterator().next()
            default:
                throw new Exception("many repo found ${reposss}")
        }
    }

    boolean isRepoGood(URIish urIish){
        String uri3 = urIish.getPath().replace('/', '')
        return isRepoGood(uri3);
    }

    abstract boolean isRepoGood(String uri3);

}
