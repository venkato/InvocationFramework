package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains;

import java.util.logging.Logger;

@CompileStatic
enum MaryDependentMavenIds implements MavenIdContains{

    jwordnet('net.sf.jwordnet:jwnl:1.3.3'),
    math_jama('gov.nist.math:jama:1.0.3'),
    opennlp_maxent('org.apache.opennlp:opennlp-maxent:3.0.3'),
    opennlp_tools('org.apache.opennlp:opennlp-tools:1.5.3'),
    ;

    MavenId m;

    MaryDependentMavenIds(String m) {
        this.m = new MavenId(m)
    }
}
