package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef

@CompileStatic
enum ConsoleProgramEnum {

    sh(new ClRef('net.sf.jremoterun.utilities.nonjdk.shell.console.GroovyShellRunnerFromConsole')),
//            sh                          ( new ClRef('net.sf.jremoterun.utilities.nonjdk.shell.GroovyShellRunner'),
    k(new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.ProgrammWinKill')),
    p(new ClRef(ProxyConsolePrograms)),
    gen(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrConfigGenerator')),
    addF(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.AddFilesToClassLoader')),
    cm(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.ConsoleCompiler')),
    jad(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrConsoleDecompiler')),
    jar(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrJarRunner')),
    asm(new ClRef('net.sf.jremoterun.utilities.nonjdk.asmow2.AsmConsoleDecompiler')),
    svn(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrSvnConsole')),
    j2g(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.Java2GroovyConverter')),
    gc2(new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.GitCheckoutConsole')),
    gradleWrapper(new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.GradleWrapperRunner')),



    classAnalyze(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.ClassAnalyze')),
    idea(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.JrrIdeaGenerator')),
    downloadMavenId(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.DropshipDown3')),
    dependencyChecker(new ClRef('net.sf.jremoterun.utilities.nonjdk.depanalise.DependencyChecker')),
    classpathStatus(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.console.ClasspathStatus')),
    ;

    public ClRef clRef;

    ConsoleProgramEnum(ClRef clRef) {
        this.clRef = clRef
    }
}
