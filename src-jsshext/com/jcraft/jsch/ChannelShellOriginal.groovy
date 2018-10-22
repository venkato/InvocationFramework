package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ChannelShellOriginal extends ChannelShell{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ChannelShellOriginal() {
        super()
    }

    @Override
    void init() throws JSchException {
        super.init()
    }

    @Override
    protected void sendRequests() throws Exception {
        super.sendRequests()
    }

    @Override
    void setLocalWindowSizeMax(int foo) {
        super.setLocalWindowSizeMax(foo)
    }

    @Override
    void setLocalWindowSize(int foo) {
        super.setLocalWindowSize(foo)
    }

    @Override
    void setLocalPacketSize(int foo) {
        super.setLocalPacketSize(foo)
    }

    @Override
    void setRemoteWindowSize(long foo) {
        super.setRemoteWindowSize(foo)
    }

    @Override
    void addRemoteWindowSize(long foo) {
        super.addRemoteWindowSize(foo)
    }

    @Override
    void setRemotePacketSize(int foo) {
        super.setRemotePacketSize(foo)
    }

    @Override
    void write(byte[] foo) throws IOException {
        super.write(foo)
    }

    @Override
    void write(byte[] foo, int s, int l) throws IOException {
        super.write(foo, s, l)
    }

    @Override
    void write_ext(byte[] foo, int s, int l) throws IOException {
        super.write_ext(foo, s, l)
    }

    @Override
    void eof_remote() {
        super.eof_remote()
    }

    @Override
    void eof() {
        super.eof()
    }

    @Override
    void close() {
        super.close()
    }

    @Override
    void setSession(Session session) {
        super.setSession(session)
    }


    @Override
    void setExitStatus(int status) {
        super.setExitStatus(status)
    }

    int getConnectTimeout2(){
        return super.connectTimeout;
    }

    Thread getThread2(){
        return super.thread;
    }
}
