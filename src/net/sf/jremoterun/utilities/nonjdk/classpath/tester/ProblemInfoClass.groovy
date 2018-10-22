package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemInfo;

import java.util.logging.Logger;

@CompileStatic
class ProblemInfoClass extends ProblemInfo{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String clazz

}
