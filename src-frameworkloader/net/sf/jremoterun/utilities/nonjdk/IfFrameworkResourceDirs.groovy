package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

@CompileStatic
enum IfFrameworkResourceDirs implements ToFileRefSelf, ChildFileLazy, ToFileRef2{

    resources_groovy,
    resources,
    log4j2_config,
    ;

    String dirName;

    IfFrameworkResourceDirs() {
        dirName = name().replace('_','-')
    }



    @Override
    File resolveToFile() {
        if(InfocationFrameworkStructure.ifDir==null){
            throw new NullPointerException("if dir is null")
        }
        return InfocationFrameworkStructure.ifDir.child(this.dirName)
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }


    public static List<IfFrameworkResourceDirs> all= values().toList()

}