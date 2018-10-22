package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.JrrJschSessionOriginal
import com.jcraft.jsch.JrrJschStaticUtils
import com.jcraft.jsch.JrrSchSessionLog
import com.jcraft.jsch.UserInfo
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.auth.AuthState
import net.sf.jremoterun.utilities.nonjdk.sshsup.channels.JrrJschSessionMethods
import net.sf.jremoterun.utilities.nonjdk.sshsup.channels.JschChannelType

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class JrrJschSession extends JrrJschSessionOriginal  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //JrrJschIO jrrJschIO = JrrJschIO.createJrrJschIOAndSet(this)

    public volatile static boolean logSessionSettingsDefault = false
    public volatile boolean logSessionSettings = logSessionSettingsDefault;
    public static int connectionTimeoutOverrideDefault = -1;;
    public int connectionTimeoutOverride = connectionTimeoutOverrideDefault;
    public ConnectionState connectionState = ConnectionState.notInited;

    public JcraftConnectopnOpener jcraftConnectopnOpener

    public volatile static JrrSchSessionLog jrrSchSessionLogDefault = new JrrSchSessionLog();
    public volatile JrrSchSessionLog jrrSchSessionLog = jrrSchSessionLogDefault;

    public List<AuthState> auths = []
    public Throwable exceptionFromConnect;
    public final Date startDate = new Date();

    JrrJschSession(JSch jsch, String username, String host, int port) throws JSchException {
        super(jsch, username, host, port)
    }


    String getConnectionStateHuman(){
        if(connectionState == ConnectionState.AuthPassed){
            return 'auth passed'
        }
        if(connectionState == ConnectionState.notInited){
            return 'not inited'
        }
        if(connectionState == ConnectionState.inProgressConnected){
            if(auths.size()==0){
                return "inProgressConnected, auth state nothing"
            }
            return "inProgressConnected, auth state : ${auths.last()}"
        }
        return connectionState.toString();
    }

    @Override
    String getConfig(String key) {
        String result =  super.getConfig(key)
        if(logSessionSettings) {
            log.info "resolved session config : ${key} = ${result}"
        }
        return result;
    }

    @Override
    Channel openChannel(String type) throws JSchException {
        if (!isConnected()) {
            throw new JSchException("session is down");
        }

        JschChannelType typeE = JschChannelType.allMap.get(type)
        log.info "openning channel type = ${type}, ${typeE}"
        jrrSchSessionLog.logMsg "opening channel : ${type}";
        if (typeE == null) {
            return openChannel3(type)
        }
        return openChannel2(typeE)
//            Channel channel=Channel.getChannel(type);
//            addChannel(channel);
//            channel.init();
//            if(channel instanceof ChannelSession){
//                applyConfigChannel((ChannelSession)channel);
//            }
//            return channel;

    }

    Channel openChannel2(JschChannelType type) throws JSchException {
        Channel channel = type.clazz.newInstance();
//        if (channel instanceof JrrJschSessionMethods) {
//            JrrJschSessionMethods  sessionMethods = (JrrJschSessionMethods) channel;
//            sessionMethods.setJrrJschSession(this)
//        }
        addChannel(channel);
        JrrJschStaticUtils.callChannelInit(channel)
        if (JrrJschStaticUtils.isChannelSession(channel)) {
            JrrClassUtils.invokeJavaMethod(this, 'applyConfigChannel', channel)
            //applyConfigChannel((ChannelSession)channel);
        }
        return channel;
    }

    @Override
    void disconnect() {
        SimpleDateFormat sdf = new SimpleDateFormat('dd HH:mm');
        jrrSchSessionLog.logMsg("disconnect ${sdf.format(new Date())}");
        super.disconnect()
        connectionState = ConnectionState.disconnected;
        log.info "disconnected ${getHost()}"
    }

    Channel openChannel3(String type) throws JSchException {
        return super.openChannel(type)
    }



    @Override
    void connect(int connectTimeout) throws JSchException {
        if(connectionTimeoutOverride>=0){
            connectTimeout = connectionTimeoutOverride;
        }

        jrrSchSessionLog.logMsg "connecting with timeout ${connectTimeout} ms ..";
//        if(jcraftConnectopnOpener.conSet2.user!=null){
//            setUserName(jcraftConnectopnOpener.conSet2.user)
//        }
//        if(jcraftConnectopnOpener.conSet2.password!=null){
//            setPassword(jcraftConnectopnOpener.conSet2.password)
//        }
        UserInfo userInfo1 = getUserInfo();
        if(userInfo1!=null){
            if (userInfo1 instanceof UserInfoJrr) {

            }else{
                UserInfoJrr u = new UserInfoJrr(userInfo1,this)
                setUserInfo(u)
            }
        }
        try {
            super.connect(connectTimeout)
        }catch(Throwable e) {
            exceptionFromConnect = e
            jrrSchSessionLog.logMsg "exception  : ${e}";
            throw e
        }finally {
            if (isAuthed2()) {
                connectionState = connectionState.AuthPassed;
            } else {
                if (connectionState == connectionState.inProgressConnected) {
                    connectionState = connectionState.AuthFailed;
                } else {
                    connectionState == connectionState.ConnectionFailed
                }

            }
        }
    }

    boolean isAuthed2(){
        return (Boolean)JrrClassUtils.getFieldValue(this,'isAuthed');
    }

    void onConnected() {
        connectionState = connectionState.inProgressConnected;
        long diff = System.currentTimeMillis() - startDate.getTime()
        diff = (long)(diff/1000)
        jrrSchSessionLog.logMsg "connected within ${diff}s";
    }

    String getPassword2() {
        return (String)JrrClassUtils.getFieldValue(this,'password')
    }

}
