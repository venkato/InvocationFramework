package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect

@CompileStatic
enum JrrStarterJarRefs2 implements ToFileRefRedirect {

    jremoterun, jrrassist, groovy_custom, groovy,
    ;


    public FileChildLazyRef ref;

    JrrStarterJarRefs2() {
        ref = GitSomeRefs.starter.childL('libs/copy/' + name() + '.jar')
    }

    String getJarName(){
        return name() + '.jar';
    }

    @Override
    ToFileRef2 getRedirect() {
        ref;
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }
}
