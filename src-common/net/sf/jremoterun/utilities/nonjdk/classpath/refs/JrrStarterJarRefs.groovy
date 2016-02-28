package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrStarterJarRefs {

    public static GitRef jremoterun = new GitRef(GitReferences.starter,"libs/origin/jremoterun.jar")

    public static GitRef jrrassist = new GitRef(GitReferences.starter,"libs/origin/jrrassist.jar")

//    public static GitRef jrrutilities = new GitRef(GitReferences.starter,"onejar/jrrutilities.jar")

}
