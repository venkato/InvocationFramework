package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper
import net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod
import quickfix.Connector
import quickfix.DataDictionary
import quickfix.DefaultDataDictionaryProvider
import quickfix.Dictionary
import quickfix.Message
import quickfix.SLF4JLogFactory
import quickfix.Session
import quickfix.SessionID
import quickfix.SessionSettings
import quickfix.SessionState
import quickfix.SocketAcceptor
import quickfix.SocketInitiator
import quickfix.field.ApplVerID
import quickfix.field.MsgType
import quickfix.field.Text

import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern;

@CompileStatic
abstract class JrrQfHelper extends RstaScriptHelper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrQfDataHolder d;
    public QfRstaRunnerWithStackTrace t;


//    abstract void sendMsg();

//    abstract void custom();

//    abstract void Connect();


    void init2() {
        t = runner as QfRstaRunnerWithStackTrace;
        d = t.d;
    }

    @Override
    void init() {
        super.init()
        init2()
    }

    boolean isNeedAddMsgToTable(Message msg, boolean isOutMsg) {
        String msgType = msg.getHeader().getString(MsgType.FIELD)
        boolean isUsefull = true
        if (msgType == MsgType.HEARTBEAT) {
            isUsefull = false
        }
        if (msgType == MsgType.TEST_REQUEST) {
            isUsefull = false
        }
        if (d.autoFixSeqNum && msgType == MsgType.LOGOUT) {
            if (msg.isSetField(Text.FIELD)) {
                onLogoutWithText(msg, isOutMsg)
            }
        }
        return isUsefull
    }

    void onLogoutWithText(Message msg, boolean isOutMsg) {
        String textLogout = msg.getString(Text.FIELD)
        Matcher matcher = d.seqNumMismatch.matcher(textLogout)
        if (matcher.matches()) {
            int expected = matcher.group(1) as int
            int my = matcher.group(2) as int;
            fixSeqNum(expected, my, isOutMsg)
        }
    }

    void fixSeqNum(int expected, int my, boolean isOutMsg) {
        log.info "expected = ${expected}, my = ${my}, isOutMsg = ${isOutMsg} "
        if (!isOutMsg) {
            int newExpected = expected + 1
            t.logs.append("setting newExpected = ${newExpected} \n")
            t.modCount++
            t.showLogs()
            d.qfSession.setNextSenderMsgSeqNum(newExpected)
            d.qfSession.logon()
        }
    }


    Properties parsePropsFromString(String s) {
        Properties props4 = new Properties()
        Properties props3 = new Properties()
        StringReader stringReader = new StringReader(s)
        props3.load(stringReader)
        props3.entrySet().each {
            String key = it.getKey()
            String value = it.getValue()
            key = key.trim()
            value = value.trim()
            props4.setProperty(key, value)
        }
        return props4;
    }


    DataDictionary getAppDd() {
        if(d.qfSession==null){
            throw new Exception("Quick fix session was not inited")
        }
        DefaultDataDictionaryProvider dictionaryProvider = d.qfSession.getDataDictionaryProvider() as DefaultDataDictionaryProvider
        Map<ApplVerID, DataDictionary> map = JrrClassUtils.getFieldValue(dictionaryProvider, 'applicationDictionaries') as Map
        Collection values = map.values()
        assert values.size() == 1
        DataDictionary appDdd = values.iterator().next() as DataDictionary
        return appDdd

    }

    String prepareMsg(String msg) {

        int i = msg.indexOf('' + d.humanSep + '10=')
        if (i > 0) {
            msg = msg.substring(0, i)
        }
        if (msg.endsWith('' + d.humanSep)) {
            msg = msg.substring(0, msg.length() - 1)
        }

        int j = msg.indexOf('' + d.humanSep + '35=')
        if (j > 0) {
            String msgForCheckSum = msg.substring(j)
            Pattern pattern = Pattern.compile('\\|9=\\d+\\|')
            Matcher matcher = pattern.matcher(msg)
            if (matcher.find()) {
                msg = matcher.replaceFirst('|9=' + msgForCheckSum.length() + d.humanSep)
            } else {
                if (msg.startsWith('8=')) {
                    throw new Exception("9 tag not found")
                }
                msg = '9=' + msgForCheckSum.length() + d.humanSep + msg
            }
        }


        if (!msg.startsWith('8=')) {
            String msg2 = '8=' + d.sessionID.getBeginString()
            if (msg.charAt(0) == d.humanSep) {
                msg = msg2 + msg
            } else {
                msg = msg2 + d.humanSep + msg
            }
        }


        msg = msg.replace(d.humanSep, d.fixMsgNativeSep)
        int checkSum = buildCheckSum(msg)
        DecimalFormat decimalFormat = new DecimalFormat('000', DecimalFormatSymbols.getInstance(Locale.UK))
        String checkSumS = decimalFormat.format(checkSum)
        msg += '' + d.fixMsgNativeSep + '10=' + checkSumS + d.fixMsgNativeSep

        return msg
    }

    void sendMsgDirectly(Message message, int seqNum) {
        JrrClassUtils.invokeJavaMethod(d.qfSession, 'sendRaw', message, seqNum)
    }


    void sendMsgDirectly2(String message) {
        JrrClassUtils.invokeJavaMethod(d.qfSession, 'send', message)
    }



    int buildCheckSum(String msg) {
        int length = msg.length()
        int checkSum = 0;
        for (int i = 0; i < length; i++) {
            char charAt = msg.charAt(i)
            checkSum += charAt
        }
        checkSum = checkSum + 1
        checkSum = checkSum % 256
        return checkSum
    }

    void initSession(Properties props3) {
        d.lastMsgSend = new Date();
        d.modificationsCount++
        d.application = new JrrQfApplication(t)
        t.statusField.setText("Staring")
        t.statusField.setForeground(Color.YELLOW)
        createSessionSettings()
        d.prop2 = props3
        d.settings.set(d.prop2)

        createSessionId()
        d.dictionary = new Dictionary(d.sessionID.toString(), d.prop2)
        d.settings.set(d.sessionID, d.dictionary)
//        SessionSettings settings = new SessionSettings(new ByteArrayInputStream(s.getBytes()));
        createLogFactory()
        createConnector()
//        d.qfSession = Session.lookupSession(d.sessionID)

        Runnable r = { onTimeout() };
        if (d.timerPeriod == null) {
            d.timerPeriod = new TimerPeriod(d.autoDisconnectTimeInSec * 1000, r)
            d.timerPeriod.start()
        }
        d.timerPeriod.start()
        updateLastRun()
    }

    SessionID createSessionId() {
        String beginString = d.settings.getString(SessionSettings.BEGINSTRING)
        String senderCompID = d.settings.getString(SessionSettings.SENDERCOMPID)
        String targetCompID = d.settings.getString(SessionSettings.TARGETCOMPID)
        d.sessionID = new SessionID(beginString, senderCompID, targetCompID)
        return d.sessionID
    }

    void createSessionSettings() {
        d.settings = new SessionSettings();
    }

//    DefaultSessionScheduleFactory createSessionScheduleFactory() {
//        d.sessionScheduleFactory = new DefaultSessionScheduleFactory();
//        return d.sessionScheduleFactory;
//    }

    Connector createConnector() {
//        createSessionScheduleFactory()
        createSessionFactory()
        d.acceptor = d.prop2.containsKey('SocketAcceptPort')
        log.info "acceptor = ${d.acceptor}"
        if (d.acceptor) {
            assert !properties.containsKey('SocketConnectPort')
            d.connector = new SocketAcceptor(d.sessionFactory, d.settings, d.DEFAULT_QUEUE_CAPACITY)
        } else {
            assert !properties.containsKey('SocketAcceptPort')
            d.connector = new SocketInitiator(d.sessionFactory, d.settings, d.DEFAULT_QUEUE_CAPACITY)
        }
        d.connector.start();
        return d.connector
    }

    JrrQfSessionFactory createSessionFactory() {
        d.sessionFactory = new JrrQfSessionFactory(d.application, d.storeFactory, d.logFactory, d.messageFactory, this)
        return d.sessionFactory;
    }

    SessionState getSessionState() {
        SessionState sessionState = JrrClassUtils.getFieldValue(d.qfSession, 'state') as SessionState
        return sessionState
    }


    void updateLastRun() {
        d.lastMsgSend = new Date()
        d.timerPeriod.getTimerImpl().setLastRun(d.lastMsgSend);
    }


    void setAutoDisconnectTimeInSecNew(long time) {
        d.autoDisconnectTimeInSec = time
        if (d.timerPeriod != null) {
            d.timerPeriod.setPeriod(time * 1000)
        }
    }


    void onTimeout() {
        long disconeedNeededAt = d.lastMsgSend.getTime() + d.autoDisconnectTimeInSec * 1000L
        boolean needStop = System.currentTimeMillis() > disconeedNeededAt
        if (needStop) {
            stopConnectionFromTimer()
            String s = "${t.sdf.format(new Date())} : need disconnect at ${t.sdf.format(new Date(disconeedNeededAt))}  "
            log.info s
            t.logs.append(s)
            t.logs.append('\n')
            t.modCount++
            t.showLogs()
            d.timerPeriod.stop()
        } else {

        }

    }

    void stopConnectionFromTimer() {
        int modificationsCountRemember = d.modificationsCount;
        synchronized (d.disconnectLock) {
            d.disconnectLock.wait(d.autoDisconnectTimeInSec * 1000);
        }
        if (d.modificationsCount == modificationsCountRemember) {
            log.info "stopping qf connection ${new Date()}"
            t.stopCurrent()
        } else {
            log.info "count diff modificationsCountRemember = ${modificationsCountRemember}, now = ${d.modificationsCount}"
        }
    }


    void createLogFactory() {
        if (d.logFactory == null) {
            d.logFactory = new SLF4JLogFactory(d.settings);
        }
    }


    void dataDictionaryAllowAll(DataDictionary dd) {
        dd.setCheckFieldsHaveValues(false)
        dd.setCheckFieldsOutOfOrder(false)
        dd.setCheckUnorderedGroupFields(false)
        dd.setCheckUserDefinedFields(false)
    }

    Session createSession(SessionID sessionID, SessionSettings sessionSettings) {
        d.qfSession = d.sessionFactory.createSessionSuper(sessionID, sessionSettings)
        configureSession(d.qfSession)
        return d.qfSession;
    }

    void configureSession(Session session) {
    }
}
