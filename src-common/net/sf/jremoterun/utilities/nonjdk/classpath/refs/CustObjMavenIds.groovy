package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains

@CompileStatic
enum CustObjMavenIds implements MavenIdContains {

    commnonsLang('commons-lang:commons-lang:2.6')
    , git('org.eclipse.jgit:org.eclipse.jgit:5.0.1.201806211838-r')
    //, git('org.eclipse.jgit:org.eclipse.jgit:4.5.4.201711221230-r')
    , compressAbstraction('org.rauschig:jarchivelib:0.7.1')
    , zeroTurnaroundZipUtil('org.zeroturnaround:zt-zip:1.13')
    , junrar1('com.github.junrar:junrar:2.0.0')
    , slf4jApi('org.slf4j:slf4j-api:1.7.25')
    , slf4jJdkLogger('org.slf4j:slf4j-jdk14:1.7.25')
    , commonsLoggingMavenId('commons-logging:commons-logging:1.2')
    /**
     * 2.5 supports jdk 6+.
     * 2.6 supports 7+.
     */
    , commonsIo('commons-io:commons-io:2.6')
    // svn deps
    , antlrRuntime('org.antlr:antlr-runtime:3.5.2')
    , sequenceLibrary('de.regnis.q.sequence:sequence-library:1.0.3')
    , sqljet('org.tmatesoft.sqljet:sqljet:1.1.11')
    , svnKit('org.tmatesoft.svnkit:svnkit:1.9.3')
    ;


    MavenId m;

    CustObjMavenIds(String m2) {
        this.m = new MavenId(m2)
    }

    public static List<CustObjMavenIds> all = values().toList()

}