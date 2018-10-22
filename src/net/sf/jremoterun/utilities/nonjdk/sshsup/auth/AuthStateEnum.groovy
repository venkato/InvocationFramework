package net.sf.jremoterun.utilities.nonjdk.sshsup.auth

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum AuthStateEnum {
    inProgress, finishedOk, finishedFailed,
    ;


}
