package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
enum CustObjMavenIds implements MavenIdContains, ToFileRef2 {

    commnonsLang('commons-lang:commons-lang:2.6')
    ,
    @Deprecated
    git(GitMavenIds.jgit.getM().toString())
    , compressAbstraction('org.rauschig:jarchivelib:1.1.0')
    , zeroTurnaroundZipUtil('org.zeroturnaround:zt-zip:1.13')
    , junrar1('com.github.junrar:junrar:4.0.0')
    , slf4jApi('org.slf4j:slf4j-api:1.7.25')
    , slf4jJdkLogger('org.slf4j:slf4j-jdk14:1.7.25')
    , commonsLoggingMavenId('commons-logging:commons-logging:1.2')
    // TODO use eclipseJavaAstParser
    , eclipseJavaCompiler('org.eclipse.jdt:ecj:3.24.0')
    , eclipseJavaAstParser('org.eclipse.jdt:org.eclipse.jdt.core:3.24.0')
    /**
     * 2.5 supports jdk 6+.
     * 2.6 supports 7+.
     */
    , commonsIo('commons-io:commons-io:2.8.0')
    // svn deps
    , antlrRuntime('org.antlr:antlr-runtime:3.5.2')
    , sequenceLibrary('de.regnis.q.sequence:sequence-library:1.0.4')
    , sqljet('org.tmatesoft.sqljet:sqljet:1.1.12')
    , svnKit('org.tmatesoft.svnkit:svnkit:1.10.1')
    ;


    MavenId m;

    CustObjMavenIds(String m2) {
        this.m = new MavenId(m2)
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    public static List<CustObjMavenIds> all = values().toList()

}