package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

@CompileStatic
enum IfFrameworkSrcDirs implements ToFileRefSelf, ChildFileLazy, ToFileRef2{

    src_common,
    src,
    src_timmoson,
    src_groovycompiler,
    src_idw,
    src_jedi,
    src_logger_ext_methods,
    src_rsta_core,
    src_rsta_runner,
    src_jsshext,
    src_archiver,

    src_frameworkloader,

    src_idea, src_idea2,src_idea_launcher,src_idea_launcher2, src_idea_github,

    src_eclipse_starter, src_eclipse_showcmd,src_eclipse_svn,

    src_helfyutils,

    src_maven,
    src_maven_http,
    src_maven_plugin_ext,
    src_maven_launcher,
    src_java8,
    //src_java11,

    src_githubutils,
    src_gitlabutils,

    src_netbeans,

    src_idea_audio,
    src_compiler_jdk,

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


    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }



    public static List<IfFrameworkSrcDirs> dir2 = [src_common, src, src_groovycompiler, src_idw,src_frameworkloader,src_jsshext,
                                                   src_jedi, src_logger_ext_methods, src_rsta_core, src_rsta_runner,src_timmoson,
                                                   src_archiver,
                                                   src_maven_launcher,]

    public static List<IfFrameworkSrcDirs> idea= [src_idea, src_idea2,src_idea_github,]
    public static List<IfFrameworkSrcDirs> eclipse= [src_eclipse_starter, src_eclipse_showcmd, src_eclipse_svn,]

    public static List<IfFrameworkSrcDirs> all= values().toList()

}