package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.Handler;

public class JdkLog2Log4jInit {

    private static boolean inited = false;
    public static boolean printMsgOnError = true;

    public static void jdk2log4j() {
        try{
            jdk2log4jImpl();
        }catch (Throwable e){
            if(printMsgOnError) {
                System.err.println("Failed configure log4j2 : "+e);
                e.printStackTrace();
            }
            InitLogTracker.defaultTracker.addException("failed set appender for jdk logger",e);
            throw e;
        }
    }
    public static void jdk2log4jImpl() {
        if (inited) {
            return;
        }

        AddDefaultIgnoreClasses.addIgnoreClasses();
//        org.apache.logging.log4j.jul.DefaultLevelConverter

        JdkIntoLog4j2Converter logHandler = new JdkIntoLog4j2Converter();
        java.util.logging.Logger logger = java.util.logging.Logger
                .getLogger("");
        logger.addHandler(logHandler);
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            logger.removeHandler(handler);
        }
        logger.addHandler(logHandler);
        JrrClassUtils.ignoreClassesForCurrentClass.add(JdkIntoLog4j2Converter.class.getName());
        inited = true;
    }



}
