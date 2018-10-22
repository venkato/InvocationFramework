package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef;

import java.util.logging.Logger;

@CompileStatic
enum GitSomeRefs implements GitSpecRef {

    jtermGitSpec('https://github.com/JetBrains/jediterm'),

    pty4jTraff('https://github.com/traff/pty4j'),
    pty4jJetBrains('https://github.com/JetBrains/pty4j'),
    purejavacommTraff('https://github.com/traff/purejavacomm'),
    purejavacommNyholku('https://github.com/nyholku/purejavacomm'),

    rstaVenkato('https://github.com/venkato/RSTALanguageSupport'),

    javaDecompiler('https://github.com/Konloch/bytecode-viewer'),


    rstaAutoCompetionVenkato('https://github.com/venkato/AutoComplete'),

    ifFramework("https://github.com/venkato/InvocationFramework"),

    starter("https://github.com/venkato/starter3"),

    sshConsole("https://github.com/venkato/ssh-consoles"),

    firstdownloadGitRef("https://github.com/venkato/firstdownload"),


    jnaplatext('https://github.com/malyn/jnaplatext'),


    ideaPsiViewer('https://github.com/cmf/psiviewer'),

    rdesktop('https://github.com/kohsuke/properjavardp'),

    socketGuiTest('https://github.com/akshath/SocketTest'),

    eclipseBatchCompiler("https://github.com/groovy/groovy-eclipse"),

    helfy("https://github.com/xardazz/helfy"),

    eclipseFileCompiltion("https://github.com/impetuouslab/eclipse-filecompletion"),


    jnaRepo("https://github.com/venkato/jna"),

    jschDocumentationRef("https://github.com/ePaul/jsch-documentation"),

    androidR8('https://r8.googlesource.com/r8'),

    jhexViewer('https://github.com/google/binnavi'),

    // https://github.com/lbalazscs/Pixelitor
    // https://github.com/statickidz/SimpleNotepad-Swing

    eclipseGithubApi('https://github.com/eclipse/egit-github'),

    dockingFrames('https://github.com/Benoker/DockingFrames'),

    ideaDatabaseNavigator('https://bitbucket.org/dancioca/dbn'),

    mavenWagon('https://github.com/apache/maven-wagon'),

    asyncProfiler('https://github.com/jvm-profiling-tools/async-profiler'),

    ;

    GitSpec gitSpec;

    GitSomeRefs(String url) {
        this.gitSpec = new GitSpec()
        gitSpec.repo = url
    }

    @Override
    File resolveToFile() {
        return getGitSpec().resolveToFile()
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }
}
