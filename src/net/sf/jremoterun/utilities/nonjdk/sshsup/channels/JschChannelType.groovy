package net.sf.jremoterun.utilities.nonjdk.sshsup.channels

import com.jcraft.jsch.*
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

/**
  {@link com.jcraft.jsch.Channel#getChannel(java.lang.String)}
 */
@CompileStatic
enum JschChannelType implements EnumNameProvider {

    session(ChannelSessionOriginal),
    shell(JrrChannelShell),
    exec(JrrChannelExec),
    x11(ChannelX11Original),
    sftp(JrrChannelSftp),
    subsystem(ChannelSubsystem),
    forwarded_tcpip(ChannelForwardedTCPIPOriginal),
    ;

//    if(type.equals("session")){
//        return new ChannelSession();
//    }
//    if(type.equals("shell")){
//        return new ChannelShell();
//    }
//    if(type.equals("exec")){
//        return new ChannelExec();
//    }
//    if(type.equals("x11")){
//        return new ChannelX11();
//    }
//    if(type.equals("auth-agent@openssh.com")){
//        return new ChannelAgentForwarding();
//    }
//    if(type.equals("direct-tcpip")){
//        return new ChannelDirectTCPIP();
//    }
//    if(type.equals("forwarded-tcpip")){
//        return new ChannelForwardedTCPIP();
//    }
//    if(type.equals("sftp")){
//        return new ChannelSftp();
//    }
//    if(type.equals("subsystem")){
//        return new ChannelSubsystem();
//    }


    public Class clazz;

    String customName;

    JschChannelType(Class clazz) {
        this.clazz = clazz
        customName = name().replace('_', '-')
    }

    public static List<JschChannelType> all = (List) values().toList()
    public static Map<String, JschChannelType> allMap = all.collectEntries { [(it.customName): it] }

//    static JschChannelType resolveFromString(String s){
//        return all.find {it.name() == s}
//    }


}
