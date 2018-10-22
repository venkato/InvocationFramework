package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities

import java.util.logging.Logger

@CompileStatic
class ProblemCollector implements ProblemCollectorI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<ProblemInfo> list = []

    @Override
    void addProblemImpl(ProblemInfo problemInfo) {
        log.info "found problem ${problemInfo}"
        list.add(problemInfo)
    }


    String printProblemsSummary() {
        if (list.size() == 0) {
            log.info "no problems"
            return null
        } else {
            String problems = list.collect { it.toString() }.join('\n')
            log.info "found ${list.size()} problems: \n${problems}"
            return problems
        }
    }

    void checkIfProblemExistAndThrowException() {
        String problemsSummary = printProblemsSummary()
        if (list.size() == 1) {
            ProblemInfo problemInfo = list[0]
//            if(problemInfo.stackTrace ==null||problemInfo.stackTrace instanceof JustStackTrace){
            throw new ProblemFoundException(problemInfo)
//            }else{
//
//            }
        }
        if (list.size() > 1) {
            throw new Exception(problemsSummary)
        }
    }

    boolean checkIfProblemExistAndShowException() {
        String problemsSummary = printProblemsSummary()
        int size = list.size()
        if(size==0){
            return true
        }
        if (size == 1) {
            ProblemInfo problemInfo = list[0]
            JrrUtilities.showException(problemInfo.msg,problemInfo.stackTrace)
        }
        if (size > 1) {
            JrrUtilities.showException(problemsSummary,new Exception(problemsSummary))
        }
        return false
    }



}
