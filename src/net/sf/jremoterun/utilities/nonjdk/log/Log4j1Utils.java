package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JavaVMClient;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.logging.Logger;


public class Log4j1Utils {
	private static final Logger log = Logger.getLogger(Log4j1Utils.class.getName());
	// private static final Logger log = logger;

	public static String sep = "\n";

	public static boolean printMsgOnError = true;

	public static Level logRootLevel = Level.INFO;

	public static Log4j1PatternLayout pl = new Log4j1PatternLayout();

	public static void setLog4jAppender() throws Exception {
		try{
		setLog4jAppenderImpl();
		}catch (Throwable e){
			if(printMsgOnError) {
				System.err.println("Failed configure log4j2 : "+e);
				e.printStackTrace();
			}
			InitLogTracker.defaultTracker.addException("failed set appender for log4j1",e);
			throw e;
		}
	}

	public static void setLog4jAppenderImpl() throws Exception {
		JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
//		if(true) {
		Log4jMigrateUtils.setLog4jAppender();
//			return;
//		}
//		LogManager.resetConfiguration();
//
//		final MBeanServer beanServer = JrrUtils.findLocalMBeanServer();
//		for (final ObjectName objectName : beanServer.queryNames(Log4jConfigurator.queryLog4jObjectName, null)) {
//			if (!objectName.equals(JavaVMClient.log4jHierarchyObjectName)
//					&& !objectName.equals(Log4jConfigurator.loggerLoggerObjectName)
//					&& !objectName.equals(JavaVMClient.log4jHierarchyObjectName)) {
//				try {
//					beanServer.unregisterMBean(objectName);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		org.apache.log4j.ConsoleAppender ca = new ConsoleAppender(pl);
//		ca.setName("IfMain");
//		org.apache.log4j.Logger.getRootLogger().addAppender(ca);
//		org.apache.log4j.Logger.getRootLogger().setLevel(logRootLevel);
//		try {
//			Log4jConfigurator.registerLog4jLoggersMBeansUsingCreateMethods();
//		} catch (Exception e) {
//			InitLogTracker.defaultTracker.addException("failed set appender for log4j1",e);
//			Throwable rootException = JrrUtils.getRootException(e);
//			if (rootException instanceof ClassNotFoundException) {
//				ClassNotFoundException cnfe = (ClassNotFoundException) rootException;
//				log.info(cnfe + "");
//			}
//
//		}
	}



}
