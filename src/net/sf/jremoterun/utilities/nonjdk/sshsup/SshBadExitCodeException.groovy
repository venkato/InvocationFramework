package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class SshBadExitCodeException extends Exception{


    SshBadExitCodeException(String var1) {
        super(var1)
    }
}
