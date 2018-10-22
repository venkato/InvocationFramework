package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ChannelForwardedTCPIPOriginal extends ChannelForwardedTCPIP{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void getData(Buffer buf) {
        super.getData(buf)
    }
}
