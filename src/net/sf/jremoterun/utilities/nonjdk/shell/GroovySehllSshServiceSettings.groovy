package net.sf.jremoterun.utilities.nonjdk.shell

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class GroovySehllSshServiceSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void  setSshProps(){
        System.setProperty(org.apache.sshd.common.io.IoServiceFactoryFactory.getName(),org.apache.sshd.netty.NettyIoServiceFactoryFactory.getName())
    }


}
