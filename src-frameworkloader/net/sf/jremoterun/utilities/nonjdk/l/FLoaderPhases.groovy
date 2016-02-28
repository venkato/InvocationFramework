package net.sf.jremoterun.utilities.nonjdk.l;

import net.sf.jremoterun.utilities.groovystarter.seqpattern.FinalPhase
import net.sf.jremoterun.utilities.groovystarter.seqpattern.JrrRunnerPhaseI
import net.sf.jremoterun.utilities.groovystarter.seqpattern.NextPhaseEnumUtil;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum FLoaderPhases implements JrrRunnerPhaseI{

    loadInitClassPath,
    // setting git
    settingGitrepo,
    loadAppClassPath,
    initApp,
    ;



    @Override
    public JrrRunnerPhaseI nextPhase() {
        JrrRunnerPhaseI nextPahse2 = NextPhaseEnumUtil.nextPhase2(this, values()) as JrrRunnerPhaseI
        return nextPahse2
    }
}
