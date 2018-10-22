package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod
import quickfix.Connector
import quickfix.DefaultMessageFactory
import quickfix.Dictionary
import quickfix.LogFactory
import quickfix.MemoryStoreFactory
import quickfix.MessageFactory
import quickfix.MessageStoreFactory
import quickfix.Session
import quickfix.SessionID
import quickfix.SessionSettings

import java.text.SimpleDateFormat;
import java.util.logging.Logger
import java.util.regex.Pattern;

@CompileStatic
class JrrQfDataHolder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public QfRstaRunnerWithStackTrace t
    public JrrQfApplication application
    public JrrQfDataHolder d = this


    public Pattern seqNumMismatch = Pattern.compile('MsgSeqNum too low, expecting (\\d+) but received (\\d)')

    public boolean autoFixSeqNum = true


    public SimpleDateFormat sdf = new SimpleDateFormat('HH:mm:ss')

    public volatile int modificationsCount = 100;

    public final Object disconnectLock = new Object()

    public Date lastMsgSend = new Date()
    public long autoDisconnectTimeInSec = 60 * 10;
    public static char fixMsgNativeSep = '\u0001';
    public char humanSep = '|';


    public MessageStoreFactory storeFactory =  new MemoryStoreFactory();
    public LogFactory logFactory
    public MessageFactory messageFactory = new DefaultMessageFactory();

    public Session qfSession

    public Connector connector;

    public Dictionary dictionary;

    public SessionID sessionID;
    public Properties prop2;
    public SessionSettings settings;

    public TimerPeriod timerPeriod
    public JrrQfSessionFactory sessionFactory
//    public DefaultSessionScheduleFactory sessionScheduleFactory
    public int DEFAULT_QUEUE_CAPACITY = 10000;
    public boolean acceptor

    JrrQfDataHolder(QfRstaRunnerWithStackTrace t) {
        this.t = t
    }
}
