package net.sf.jremoterun.utilities.nonjdk.classpath;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.git.GitRef;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CutomJarAdd {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? super GitRef> gitRefs = (List) [
            GitReferences.rsta,
            GitReferences.rstaAutoCompetion,
    ]



    static void addCustom(AddFilesToClassLoaderCommon adder) {
        adder.addAll gitRefs
//        adder.addAll JeditermBinRefs.all
    }

}
