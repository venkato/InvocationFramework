package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.HostCheckResultEnum
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JrrKnowHostOriginal
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrJrrKnowHost extends JrrKnowHostOriginal{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrJrrKnowHost(JSch jsch) {
        super(jsch)
    }


    @Override
    int check(String host, byte[] key) {
        int result =  super.check(host, key)
        HostCheckResultEnum resultEnum = HostCheckResultEnum.statusMap.get(result)
        log.info "${host} host key result ${resultEnum}"
        return result
    }
}
