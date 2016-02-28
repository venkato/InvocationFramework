package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;

import java.util.Collection;
import java.util.logging.Logger;


public class Log4j2Utils {
    private static final Logger log = Logger.getLogger(Log4j2Utils.class.getName());
    // private static final Logger log = logger;

    public static String sep = "\n";

    public static Level logRootLevel = Level.INFO;

    public static org.apache.logging.log4j.core.Logger rootLogger;

    public static volatile Log4j2PatternLayout pl = new Log4j2ColorPatternLayout();
//    public static volatile Log4j2PatternLayout pl = InitGeneral.useColorOutput?new Log4j2ColorPatternLayout():new Log4j2PatternLayout();
//    public static volatile Log4j2PatternLayout pl = new Log4j2ColorPatternLayout();

    public static void setLog4jAppender() throws Exception {
        checkAndFixFactory();
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        ConsoleAppender ca = ConsoleAppender.createDefaultAppenderForLayout(pl);
        org.apache.logging.log4j.Logger rootLogger3 = LogManager.getRootLogger();
        ClassPathTesterHelper.checkClassInstanceOf(rootLogger3, org.apache.logging.log4j.core.Logger.class);
        rootLogger = (org.apache.logging.log4j.core.Logger) rootLogger3;
        LoggerConfig loggerConfig = rootLogger.get();
//		loggerConfig.stop();
//		assertTrue(loggerConfig.isStopped());
        loggerConfig.setLevel(logRootLevel);
        Collection<Appender> appenders = rootLogger.getAppenders().values();
        for (Appender appender : appenders) {
            rootLogger.removeAppender(appender);
        }
        ca.start();
        rootLogger.addAppender(ca);
//		loggerConfig.start();
    }

    public static void checkAndFixFactory() throws Exception {
        System.setProperty(LogManager.FACTORY_PROPERTY_NAME, Log4jContextFactory.class.getName());
        System.setProperty((String) JrrClassUtils.getFieldValue(ThreadContextMapFactory.class, "THREAD_CONTEXT_KEY"), DefaultThreadContextMap.class.getName());
        boolean needSetFactory = false;
        LoggerContextFactory factory = (LoggerContextFactory) JrrClassUtils.getFieldValue(LogManager.class, "factory");
        if (factory == null) {
            log.info("factory is null");
            needSetFactory = true;
        } else if (factory.getClass() != Log4jContextFactory.class) {
            log.info("factory strange : " + factory.getClass().getName());
            needSetFactory = true;
        } else {

        }
        if (needSetFactory) {
            factory = new Log4jContextFactory();
            JrrClassUtils.setFieldValue(LogManager.class, "factory", factory);
        }
    }


    public static void setLogLevel(String loggerName, org.apache.log4j.Level level) {
        org.apache.log4j.Logger.getLogger(loggerName).setLevel(level);
        Level level2 = Log4jIntoLog4j2Converter.log4j1ToLog4j2Map.get(level);
        Configurator.setLevel(loggerName, level2);
    }


    static void checkLogger2() {
        LoggerContext context = LogManager.getContext(false);

    }

    static void setLogLevel(Class clazz, org.apache.log4j.Level level) {
        setLogLevel(clazz.getName(), level);
    }

}
