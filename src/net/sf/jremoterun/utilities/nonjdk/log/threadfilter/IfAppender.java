package net.sf.jremoterun.utilities.nonjdk.log.threadfilter;

import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import sun.reflect.Reflection;
import timmoson.common.sertcp.TcpSession;

import java.util.HashSet;

public class IfAppender   extends AbstractAppender {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public TcpSession session;

	public Log4jAppenderListener appenderListener;

	public Thread loggingThread;

	public HashSet<String> loggingLogger = new HashSet<String>();

	public boolean translateAllLoggers = false;
	public boolean translateAllLoggersForExecuterThread = false;

	public IfAppender(TcpSession session) throws Exception {
		super("RemoteAppender", null, null);
		this.session = session;
		appenderListener = session.makeClient(Log4jAppenderListener.class,
				Log4jAppenderListener.class.getName());
		loggingThread = Thread.currentThread();
	}

	public void addClassToLog4jTranslate(Class clazz) {
		loggingLogger.add(clazz.getName());
	}


	@Override
	public void append(LogEvent loggingEvent) {
		if (session.isClosed()) {
			log.debug("session is closed");
		} else {
			boolean isLog = translateAllLoggers
					|| (Thread.currentThread() == loggingThread && translateAllLoggersForExecuterThread)
					|| loggingLogger.contains(loggingEvent.getLoggerName());
			if (IfAppender.class.getName().equals(loggingEvent.getLoggerName())) {
				isLog = false;
			}
			if (isLog) {
				long millis = System.currentTimeMillis();
				StringBuilder sb = new StringBuilder();
				Log4jAppenderListener.patternLayout.formatImpl2(loggingEvent, sb, new Throwable().getStackTrace());
				sb.setLength(sb.length() - JdkLogFormatter.sep.length());
				try {
					appenderListener.add(sb.toString());
					long diff = System.currentTimeMillis() - millis;
					diff = diff / 1000;
					if (diff > 1) {
						String msg = "too long " + diff + " sec " + loggingEvent.getMessage();
						org.apache.logging.log4j.Level level = loggingEvent.getLevel();
						if (!level.isMoreSpecificThan(org.apache.logging.log4j.Level.ERROR)) {
							level = org.apache.logging.log4j.Level.ERROR;
						}

						LogManager.getLogger(loggingEvent.getLoggerName()).log(level, msg, loggingEvent.getThrown());

					}
				} catch (Exception e) {
					log.info(loggingEvent.getMessage(), e);
				}
			}
			// }
		}
	}


}
