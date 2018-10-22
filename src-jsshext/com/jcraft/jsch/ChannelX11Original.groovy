package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ChannelX11Original extends ChannelX11{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ChannelX11Original() {
    }
}
