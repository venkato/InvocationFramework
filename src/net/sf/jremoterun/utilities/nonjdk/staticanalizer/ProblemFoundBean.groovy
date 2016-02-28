package net.sf.jremoterun.utilities.nonjdk.staticanalizer;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ProblemFoundBean implements Comparable<ProblemFoundBean>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    StaticElementInfo pb
    String msg;

    @Override
    int compareTo(ProblemFoundBean o) {
        int res =  pb.className.compareTo(o.pb.className);
        if(res!=0){
            return res
        }
        return pb.lineNumber.compareTo(o.pb.lineNumber)
    }
}
