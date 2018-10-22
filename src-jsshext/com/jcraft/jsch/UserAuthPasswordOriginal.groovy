package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class UserAuthPasswordOriginal extends UserAuthPassword{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    boolean start(Session session) throws Exception {
        return super.start(session)
    }

}
