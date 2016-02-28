package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import sun.net.www.protocol.http.HttpURLConnection
import sun.util.logging.PlatformLogger

import java.util.logging.Level;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JdkCoreMethods {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static boolean platformRedirectDone = false

    static void setDefaultLevel(PlatformLogger.Level level){
        JrrClassUtils.setFieldValue(PlatformLogger,'DEFAULT_LEVEL',level)
    }


    static void redirectPlatformLoggers(){
        if(platformRedirectDone){
            log.info "redirect already done"
        }else {
            platformRedirectDone = true
            PlatformLogger.redirectPlatformLoggers();
        }
    }

    static void enableHttpLogging(){
        JdkLogFormatter.setLogFormatter();
        JdkLogFormatter.findConsoleHandler().setLevel(Level.ALL)
        PlatformLogger logger = HttpURLConnection.getHttpLogger()
        logger.setLevel(PlatformLogger.Level.ALL)
        redirectPlatformLoggers();
    }

}
