package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef

@CompileStatic
enum JrrStarterProjects implements GitRefRef {

    firstdownload, JrrInit, JrrStarter, JrrUtilities,
    ;

    @Override
    GitRef getRef() {
        return new GitRef(GitReferences.starter,this.name())
    }

    @Override
    File resolveToFile() {
        return getRef().resolveToFile()
    }

    static void addAll(AddFilesToClassLoaderCommon adder){
        values().toList().each {
            adder.add it.resolveToFile().child('src')
        }
    }

}
