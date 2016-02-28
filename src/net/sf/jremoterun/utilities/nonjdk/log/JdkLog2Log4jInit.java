package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.Handler;

public class JdkLog2Log4jInit {

    private static boolean inited = false;

    public static void jdk2log4j() {
        if (inited) {
            return;
        }
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
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
