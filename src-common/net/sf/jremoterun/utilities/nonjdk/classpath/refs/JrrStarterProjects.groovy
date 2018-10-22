package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef

@CompileStatic
enum JrrStarterProjects implements ChildFileLazy, ToFileRef2 {

    firstdownload, JrrInit, JrrStarter, JrrUtilities,
    ;

    FileChildLazyRef refFile3;

    JrrStarterProjects() {
        refFile3 = GitSomeRefs.starter.childL(name());
    }

    @Deprecated
    GitRef getRef() {
        return new GitRef(GitSomeRefs.starter,this.name())
    }

    @Override
    File resolveToFile() {
        return refFile3.resolveToFile()
    }


    FileChildLazyRef getSrcRef(){
        return refFile3.childL('src')
    }


    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }


    static void addAll(AddFilesToClassLoaderCommon adder){
        values().toList().each {
            adder.add getSrcRef()
        }
    }


}
