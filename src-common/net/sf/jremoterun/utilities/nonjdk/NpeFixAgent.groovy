package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import org.apache.commons.lang3.SystemUtils;

import java.util.logging.Logger;

@CompileStatic
class NpeFixAgent {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static File linkToRef;
    public static String javaArgStr;

    static File getRefToRichNpeLib(){
        if(linkToRef!=null){
            return linkToRef
        }
        String suffix ;

        if(SystemUtils.IS_OS_WINDOWS){
            suffix = 'dll'
        }else{
            suffix = 'so'
        }
        FileChildLazyRef richNpeDir = GitSomeRefs.ifFramework.childL('richNpe/richNPE64.'+suffix)
        linkToRef= richNpeDir.resolveToFile();
        return linkToRef
    }

    static String buildPathString(){
        if(javaArgStr!=null){
            return javaArgStr
        }
        String path = getRefToRichNpeLib().getAbsolutePath().replace('\\','/')
        javaArgStr = "-agentpath:${path}"
        return javaArgStr
    }
}
