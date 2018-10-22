package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.Channel
import com.jcraft.jsch.IO
import com.jcraft.jsch.JrrJschSessionOriginal
import com.jcraft.jsch.JrrJschStaticUtils
import com.jcraft.jsch.JschIOOriginal
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrJschIO extends JschIOOriginal{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    JrrJschIO() {
    }

    static JrrJschIO createJrrJschIOAndSet(Channel channel){
        JrrJschIO jrrJschIO = new JrrJschIO()
        JrrJschStaticUtils.setJschIo(channel,jrrJschIO)
        return jrrJschIO
    }

    @Override
    void out_close() {
        super.out_close()
        log.info "out close"
    }
}
