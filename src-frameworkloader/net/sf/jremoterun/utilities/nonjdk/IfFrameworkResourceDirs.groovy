package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf

@CompileStatic
enum IfFrameworkResourceDirs implements ToFileRefSelf{

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



    public static List<IfFrameworkResourceDirs> all= values().toList()

}