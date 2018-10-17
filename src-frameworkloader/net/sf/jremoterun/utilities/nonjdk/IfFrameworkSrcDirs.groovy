package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf

@CompileStatic
enum IfFrameworkSrcDirs implements ToFileRefSelf{

    src_common,
    src,
    src_groovycompiler,
    src_idw,
    src_jedi,
    src_logger_ext_methods,
    src_rsta_core,
    src_rsta_runner,

    src_frameworkloader,

    src_idea, src_idea2,src_idea_launcher,src_idea_launcher2,

    src_eclipse_starter, src_eclipse_showcmd,

    src_helfyutils,
    ;

    String dirName;

    IfFrameworkSrcDirs() {
        dirName = name().replace('_','-')
    }

    @Override
    File resolveToFile() {
        if(InfocationFrameworkStructure.ifDir==null){
            throw new NullPointerException("if dir is null")
        }
        return InfocationFrameworkStructure.ifDir.child(this.dirName)
    }

    public static List<IfFrameworkSrcDirs> dir2 = [src_common, src, src_groovycompiler, src_idw,src_frameworkloader,
                                                   src_jedi, src_logger_ext_methods, src_rsta_core, src_rsta_runner,]

    public static List<IfFrameworkSrcDirs> idea= [src_idea, src_idea2,]
    public static List<IfFrameworkSrcDirs> eclipse= [src_eclipse_starter, src_eclipse_showcmd,]

    public static List<IfFrameworkSrcDirs> all= values().toList()

}