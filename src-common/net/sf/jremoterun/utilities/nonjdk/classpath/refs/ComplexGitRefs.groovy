package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect

@CompileStatic
enum ComplexGitRefs implements ToFileRefRedirect {

    eclipseGithubApiB(new BinaryWithSource2(LatestMavenIds.eclipseGitHubApi.m, GitReferences.eclipseGithubApi));


    ToFileRef2 ref

    ComplexGitRefs(ToFileRef2 ref) {
        this.ref = ref
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }

    @Override
    ToFileRef2 getRedirect() {
        return ref
    }
}
