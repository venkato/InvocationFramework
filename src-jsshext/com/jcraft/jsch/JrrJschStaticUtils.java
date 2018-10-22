package com.jcraft.jsch;

public class JrrJschStaticUtils {

    public static boolean isChannelSession(Channel channel) {
        boolean b = channel instanceof ChannelSession;
        return b;
    }

    public static void callChannelInit(Channel channel) throws JSchException {
        channel.init();
    }

    public static void setJschIo(Channel channel, IO io) {
        channel.io = io;
    }

}
