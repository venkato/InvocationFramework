package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef

@Deprecated
@CompileStatic
enum JeditermBinRefs implements GitBinaryAndSourceRefRef {

    jtermSsh(GitSomeRefs.jtermGitSpec,'build/jediterm-ssh-2.8.jar', 'ssh/src')
//    , jtermPty(GitReferences.jtermGitSpec,'build/jediterm-pty-2.10.jar', 'pty/src')
//    ,pty4j ('https://github.com/traff/pty4j', 'build/pty4j-0.7.5.jar', 'build/pty4j-0.7.5-src.jar')
    ;

    GitBinaryAndSourceRef ref;

    JeditermBinRefs(GitSpecRef repo, String bin, String src) {
        ref = new GitBinaryAndSourceRef(repo, bin, src)
    }

    JeditermBinRefs(String repo,String bin, String src) {
        ref = new GitBinaryAndSourceRef(repo, bin, src)
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }


    public static List<JeditermBinRefs> all = values().toList()



    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

}
