package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec

@CompileStatic
class GitReferences {


    public static GitBinaryAndSourceRef jtermSsh = JeditermBinRefs.jtermSsh.ref

    public static GitBinaryAndSourceRef jtermPty = JeditermBinRefs.jtermPty.ref

    public static GitRef jtermSrc = new GitRef('https://github.com/JetBrains/jediterm', 'terminal/src')

    public static GitRef pty4jLinuxLibs = new GitRef('https://github.com/traff/pty4j', 'os')

    public
    static GitBinaryAndSourceRef rsta = new GitBinaryAndSourceRef('https://github.com/venkato/RSTALanguageSupport', 'build/rsta.jar', 'src/main/java')

    public
    static GitBinaryAndSourceRef javaDecompiler = new GitBinaryAndSourceRef('https://github.com/Konloch/bytecode-viewer', 'BytecodeViewer 2.9.8.jar', 'src/main/java')


    public
    static GitBinaryAndSourceRef rstaAutoCompetion = new GitBinaryAndSourceRef('https://github.com/venkato/AutoComplete', 'dist/AutoComplete.jar', 'src/main/java')

    public static GitSpec ifFramework = new GitSpec("https://github.com/venkato/InvocationFramework")

    public static GitSpec starter = new GitSpec("https://github.com/venkato/starter3")

    public static GitRef groovyRunner = new GitRef(starter, 'firstdownload/groovyrunner.groovy')

    public static GitRef groovyClasspathDir = new GitRef(starter, 'libs/copy')

    public static GitRef sshConsole = new GitRef("https://github.com/venkato/ssh-consoles", "src")

    public static GitRef firstdownloadGitRef = new GitRef("https://github.com/venkato/firstdownload", "src")

    public static GitRef sshConsoleImages = new GitRef(sshConsole.repo, "images")


    public static GitRef jnaplatext = new GitRef('https://github.com/malyn/jnaplatext', 'src')


    public static GitSpec rdesktop = new GitSpec('https://github.com/kohsuke/properjavardp')

    public
    static GitBinaryAndSourceRef socketGuiTest = new GitBinaryAndSourceRef('https://github.com/akshath/SocketTest', 'dist/SocketTest.jar', 'src')

    public
    static GitRef eclipseBatchCompiler = new GitRef("https://github.com/groovy/groovy-eclipse", "base/org.eclipse.jdt.groovy.core/src")

    public static GitRef helfySrc = new GitRef("https://github.com/xardazz/helfy", "src")

    public static GitRef helfyTest = new GitRef("https://github.com/xardazz/helfy", "test")

    public
    static GitRef eclipseFileCompiltion = new GitRef("https://github.com/impetuouslab/eclipse-filecompletion", "filecompletion/org.impetuouslab.eclipse.filecompletion/src")


    public static GitSpec jnaRepo = new GitSpec("https://github.com/venkato/jna")


    public
    static GitBinaryAndSourceRef jnaJvmti = new GitBinaryAndSourceRef(jnaRepo, 'build/jvmti.jar', 'src-jvmtiutils')

    public static GitBinaryAndSourceRef jnaCore = new GitBinaryAndSourceRef(jnaRepo, 'build/jna_core.jar', 'jna/src')

    public static GitRef jnaJvmtiResourcesDir = new GitRef(jnaRepo, 'jvmti-resources')

    public static GitRef androidR8 = new GitRef('https://r8.googlesource.com/r8', 'src/main/java')

    public static GitSpec jhexViewer = new GitSpec('https://github.com/google/binnavi')

    // https://github.com/lbalazscs/Pixelitor
    // https://github.com/statickidz/SimpleNotepad-Swing

    public
    static GitRef eclipseGithubApi = new GitRef('https://github.com/eclipse/egit-github', 'org.eclipse.egit.github.core/src')

    public static GitSpec dockingFrames = new GitSpec('https://github.com/Benoker/DockingFrames')


}
