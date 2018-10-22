package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef

@CompileStatic
enum JeditermBinRefs2 implements GitRefRef {

    ssh,
    pty,
    terminal,
    ;

    GitRef ref;

    JeditermBinRefs2() {
        ref = new GitRef(GitSomeRefs.jtermGitSpec, name()+'/src')
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }


    public static List<JeditermBinRefs2> all = values().toList()



    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

}
