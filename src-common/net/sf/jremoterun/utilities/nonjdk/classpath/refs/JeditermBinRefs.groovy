package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef

@CompileStatic
enum JeditermBinRefs implements GitBinaryAndSourceRefRef {

    jtermSsh('https://github.com/JetBrains/jediterm','build/jediterm-ssh-2.8.jar', 'ssh/src')
    , jtermPty('https://github.com/JetBrains/jediterm','build/jediterm-pty-2.10.jar', 'pty/src')
    ,pty4j ('https://github.com/traff/pty4j', 'build/pty4j-0.7.5.jar', 'build/pty4j-0.7.5-src.jar')
    ;

    GitBinaryAndSourceRef ref;

    JeditermBinRefs(String repo,String bin, String src) {
        ref = new GitBinaryAndSourceRef(repo, bin, src)
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }


    public static List<JeditermBinRefs> all = values().toList()
}
