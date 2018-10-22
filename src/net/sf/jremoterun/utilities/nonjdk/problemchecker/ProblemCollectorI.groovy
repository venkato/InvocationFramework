package net.sf.jremoterun.utilities.nonjdk.problemchecker

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
interface ProblemCollectorI {

    void addProblemImpl(ProblemInfo problemInfo)


}
