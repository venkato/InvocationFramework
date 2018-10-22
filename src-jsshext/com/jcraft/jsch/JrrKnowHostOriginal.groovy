package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrKnowHostOriginal extends KnownHosts{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrKnowHostOriginal(JSch jsch) {
        super(jsch)
    }

    @Override
    void setKnownHosts(InputStream input) throws JSchException {
        super.setKnownHosts(input)
    }

    @Override
    HostKey createHashedHostKey(String host, byte[] key) throws JSchException {
        return super.createHashedHostKey(host, key)
    }

    @Override
    void setKnownHosts(String filename) throws JSchException {
        super.setKnownHosts(filename)
    }

    @Override
    String getKnownHostsFile() {
        return super.getKnownHostsFile()
    }

    @Override
    void dump(OutputStream out) throws IOException {
        super.dump(out)
    }

}
