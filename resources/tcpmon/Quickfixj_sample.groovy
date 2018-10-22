import net.sf.jremoterun.utilities.nonjdk.quickfixsender.JrrQfDataHolder;
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.JrrQfHelper;
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.QfRstaRunnerWithStackTrace;

import java.util.*;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunner;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper;
import groovy.transform.CompileStatic;
import quickfix.Message;

@CompileStatic
class Quickfixj_sample extends JrrQfHelper  {
    QfRstaRunnerWithStackTrace qf;
    JrrQfDataHolder d;
     JrrQfHelper h = this;

    void r() {    	
    }

    void custom(){
    	sendMsg();
    }

    void sendMsg(){
        // 2.groovy
        String s = '|9=1|35=1';
        s= h.prepareMsg(s);
        log.info (s.replace(d.fixMsgNativeSep,d.humanSep));
        Message message = new Message(s, h.getAppDd());
        d.qfSession.send(message)
        //qf.sendMsg2();
    }

    void Connect(){
//qf.setAutoDisconnectTimeInSecNew(10)	 	;
        String s = """
ConnectionType=initiator
#ConnectionType=acceptor
ReconnectInterval=60
SenderCompID=ARCA
TargetCompID=TW
#SocketAcceptPort=6716
SocketConnectPort=6712
SocketConnectHost=127.0.0.1
HeartBtInt=20
StartTime=23:30:00
EndTime=23:30:00
BeginString=FIX.4.4
DataDictionary=FIX44.xml
RefreshOnLogon=Y
ResetOnLogon=Y

"""
//qf.stopCurrent();
        Properties props =  h.parsePropsFromString(s);
        h.initSession(props);
    }

    @Override
    void init() {
        qf = (QfRstaRunnerWithStackTrace) runner;
        d = qf.d;
        h.init2();

    }

}