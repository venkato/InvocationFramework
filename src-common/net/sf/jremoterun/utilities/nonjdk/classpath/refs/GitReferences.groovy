package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec

@CompileStatic
class GitReferences {


//    public static GitBinaryAndSourceRef jtermSsh = JeditermBinRefs.jtermSsh.ref

//    public static GitBinaryAndSourceRef jtermPty = JeditermBinRefs.jtermPty.ref


//    public static GitRef jtermSrc = new GitRef('https://github.com/JetBrains/jediterm', 'terminal/src')

    public static FileChildLazyRef pty4jLinuxLibs =   GitSomeRefs.pty4jTraff.childL('os')
    public static FileChildLazyRef pty4jSrc = GitSomeRefs.pty4jTraff.childL('src')
    public static FileChildLazyRef pty4jJetbrainsSrc =  GitSomeRefs.pty4jJetBrains.childL( 'src')
    public static FileChildLazyRef purejavacommTraffSrc = GitSomeRefs.purejavacommTraff.childL( 'src')
    public static FileChildLazyRef pureJavacommnyHolkuSrc = GitSomeRefs.purejavacommNyholku.childL(  'src')

    public
    static GitBinaryAndSourceRef rsta =   new GitBinaryAndSourceRef(GitSomeRefs.rstaVenkato, 'build/rsta.jar', 'src/main/java')

    public
    static GitBinaryAndSourceRef javaDecompiler = new GitBinaryAndSourceRef(GitSomeRefs.javaDecompiler, 'BytecodeViewer 2.9.8.jar', 'src/main/java')


    public
    static GitBinaryAndSourceRef rstaAutoCompetion = new GitBinaryAndSourceRef(GitSomeRefs.rstaAutoCompetionVenkato, 'dist/AutoComplete.jar', 'src/main/java')


    @Deprecated
    public static FileChildLazyRef groovyRunner = JrrStarterJarRefs.groovyRunner

    @Deprecated
    public static FileChildLazyRef groovyClasspathDir = JrrStarterJarRefs.groovyClasspathDir

    public static FileChildLazyRef sshConsole = GitSomeRefs.sshConsole.childL("src")

    public static FileChildLazyRef firstdownloadGitRef = GitSomeRefs.firstdownloadGitRef.childL( "src")

    public static FileChildLazyRef sshConsoleImages = GitSomeRefs.sshConsole.childL( "images")


    public static FileChildLazyRef jnaplatext = GitSomeRefs.jnaplatext.childL( 'src')



    public
    static GitBinaryAndSourceRef socketGuiTest = new GitBinaryAndSourceRef(GitSomeRefs.socketGuiTest, 'dist/SocketTest.jar', 'src')

    public
    static FileChildLazyRef eclipseBatchCompiler = GitSomeRefs.eclipseBatchCompiler.childL( "base/org.eclipse.jdt.groovy.core/src")

    public static FileChildLazyRef helfySrc = GitSomeRefs.helfy.childL( "src")

    public static FileChildLazyRef helfyTest =  GitSomeRefs.helfy.childL( "test")

    public
    static FileChildLazyRef eclipseFileCompiltion = GitSomeRefs.eclipseFileCompiltion.childL("filecompletion/org.impetuouslab.eclipse.filecompletion/src")


    public static FileChildLazyRef jschDocumentationRef = GitSomeRefs.jschDocumentationRef.childL('src/main/java')

    public static BinaryWithSource2 jschDocumentationBinWithSrc =  new BinaryWithSource2(  LatestMavenIds.jcraft, jschDocumentationRef)

    public
    static GitBinaryAndSourceRef jnaJvmti = new GitBinaryAndSourceRef(GitSomeRefs.jnaRepo, 'build/jvmti.jar', 'src-jvmtiutils')

    public static GitBinaryAndSourceRef jnaCore = new GitBinaryAndSourceRef(GitSomeRefs.jnaRepo, 'build/jna_core.jar', 'jna/src')

    @Deprecated
    public static GitRef jnaJvmtiResourcesDir = new GitRef (GitSomeRefs.jnaRepo.getGitSpec().repo,'jvmti-resources');
    public static FileChildLazyRef jnaJvmtiResourcesDir2 = GitSomeRefs.jnaRepo.childL( 'jvmti-resources')


    public static FileChildLazyRef androidR8 = GitSomeRefs.androidR8.childL( 'src/main/java')

    /**
     * Used in {@link org.jna.jvmtiutils.JnaNativeMethods}
     */
    @Deprecated
    public static GitSpec jnaRepo = GitSomeRefs.jnaRepo.gitSpec



    public
    static FileChildLazyRef eclipseGithubApi = GitSomeRefs.eclipseGithubApi.childL( 'org.eclipse.egit.github.core/src')



}
