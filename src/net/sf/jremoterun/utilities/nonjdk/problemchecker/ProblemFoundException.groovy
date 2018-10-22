package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ProblemFoundException extends Exception{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    ProblemInfo problemInfo

    ProblemFoundException( ProblemInfo problemInfo) {
        super(problemInfo.msg)
        this.problemInfo = problemInfo
        if(problemInfo.stackTrace==null || problemInfo.stackTrace instanceof JustStackTrace){

        }else{
            initCause(problemInfo.stackTrace)
        }
    }






}
