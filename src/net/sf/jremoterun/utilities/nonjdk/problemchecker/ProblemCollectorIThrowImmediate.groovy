package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ProblemCollectorIThrowImmediate implements ProblemCollectorI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void addProblemImpl(ProblemInfo problemInfo) {
        throw new ProblemFoundException(problemInfo)
    }
}
