package net.sf.jremoterun.utilities.nonjdk.classpath.git;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import org.junit.Test;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GitRefCheck {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static boolean checkRefExist2(GitRefRef gitRef){
        checkRefExist(gitRef.ref)
    }

    static boolean checkRefExist(GitRef gitRef){
        String branch = gitRef.branch
        if(branch==null){
            branch = 'master'
        }
        String url = "${gitRef.repo}/blob/${branch}/${gitRef.pathInRepo}"
        URL url1 = new URL(url)
        InputStream stream = url1.openStream()
        stream.close()
        return true
    }



    

}
