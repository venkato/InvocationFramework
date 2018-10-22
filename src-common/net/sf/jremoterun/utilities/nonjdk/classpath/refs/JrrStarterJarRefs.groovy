package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrStarterJarRefs {

    /*
    @Deprecated
    public static FileChildLazyRef jremoterun = GitSomeRefs.starter.childL( "libs/origin/jremoterun.jar")

    @Deprecated
    public static FileChildLazyRef jremoterunCopy = GitSomeRefs.starter.childL( "libs/copy/jremoterun.jar")

    @Deprecated
    public static FileChildLazyRef jrrassist = GitSomeRefs.starter.childL( "libs/origin/jrrassist.jar")

     */

//    public static FileChildLazyRef jrrutilitiesOneJar = GitSomeRefs.starter.childL( "onejar/jrrutilities.jar")

    public static BinaryWithSource2 jrrutilitiesOneJar = new BinaryWithSource2(GitSomeRefs.starter.childL( "onejar/jrrutilities.jar"), JrrStarterProjects.JrrUtilities.getSrcRef());
    

    public static FileChildLazyRef groovyRunner = GitSomeRefs.starter.childL('firstdownload/groovyrunner.groovy')

    public static FileChildLazyRef groovyClasspathDir = GitSomeRefs.starter.childL('libs/copy')




}
