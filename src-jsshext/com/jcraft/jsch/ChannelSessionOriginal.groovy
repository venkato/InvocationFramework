package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ChannelSessionOriginal extends ChannelSession{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ChannelSessionOriginal() {
    }
}
