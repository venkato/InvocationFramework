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

    public static BinaryWithSource2 jrrutilitiesOneJar = new BinaryWithSource2(GitSomeRefs.starter.childL( "onejar/jrrutilities.jar"), JrrStarterProjects.JrrUtilities.getSrcRef());
    

    public static FileChildLazyRef groovyRunner = GitSomeRefs.starter.childL('firstdownload/groovyrunner.groovy')

    public static FileChildLazyRef groovyClasspathDir = GitSomeRefs.starter.childL('libs/copy')




}
