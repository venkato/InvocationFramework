package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ProblemHelper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ProblemCollectorI problemCollectorI

    void addProblem2(String msg,Throwable stackTrace){
        ProblemInfo problemInfo = new ProblemInfo()
        problemInfo.msg = msg
        problemInfo.stackTrace = stackTrace
        addProblemImpl(problemInfo)
    }

    void addProblem(String msg){
        ProblemInfo problemInfo = new ProblemInfo()
        problemInfo.msg = msg
        problemInfo.stackTrace = new Exception(msg)
        addProblemImpl(problemInfo)
    }

    void addProblemImpl(ProblemInfo problemInfo){
        problemCollectorI.addProblemImpl(problemInfo)
    }


}
