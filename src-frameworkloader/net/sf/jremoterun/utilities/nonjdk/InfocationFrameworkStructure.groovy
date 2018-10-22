package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger

@CompileStatic
class InfocationFrameworkStructure extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static List<String> resources = IfFrameworkResourceDirs.all.collect {it.dirName}


    @Deprecated
    public static List<String> dirsIdea = IfFrameworkSrcDirs.idea.collect {it.dirName}

    @Deprecated
    public static List<String> dirsEclipsePluginStarter = IfFrameworkSrcDirs.eclipse.collect{it.dirName}

    @Deprecated
    public static String helfyUtils = IfFrameworkSrcDirs.src_helfyutils.dirName


    @Deprecated
    public static List<String> dirs2 = IfFrameworkSrcDirs.dir2.collect {it.dirName}


    @Deprecated
    public static List<String> dirs = dirs2 +  IfFrameworkResourceDirs.all.collect {it.dirName}

    @Deprecated
    public static List<String> dirs3 = dirs2 +  IfFrameworkResourceDirs.all.collect {it.dirName}

    public static File ifDir;

    @Override
    Object get(Object o) {
        JavaVersionChecker.checkJavaVersion()
        List list = (List) o;
        if (list == null) {
            throw new IllegalArgumentException("need set adder and InfocationFramework base dir")
        }
        if (list.size() != 2) {
            throw new IllegalArgumentException("need set adder and InfocationFramework base dir, but got : ${list}")
        }
        AddFilesToClassLoaderCommon adder = list[0] as AddFilesToClassLoaderCommon;

        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }
        File ifDir = list[1] as File
        if (ifDir == null) {
            throw new IllegalArgumentException("InfocationFramework base dir is null")
        }
        addRefs(adder, ifDir);
        return null;
    }

    void addImpl(AddFilesToClassLoaderCommon adder, File ifDir) {

    }

    static void addRefs(AddFilesToClassLoaderCommon adder, File ifDir2) {
        ifDir = ifDir2
        dirs3.each {
            File f = new File(ifDir2, it)
            adder.addF(f)
//            log.info "file added : ${f}"
        }
    }

}
