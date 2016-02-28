package net.sf.jremoterun.utilities.nonjdk

import junit.framework.TestCase;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.nonjdk.log.JdkLog2Log4jInit
import net.sf.jremoterun.utilities.nonjdk.log.Log4j1Utils
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import org.apache.commons.io.output.TeeOutputStream
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class InvokactionFrameWorkTest extends TestCase{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    ByteArrayOutputStream baos= new ByteArrayOutputStream();

    InvokactionFrameWorkTest() {
        TeeOutputStream teeOut = new TeeOutputStream(System.out,baos);
        TeeOutputStream teeErr = new TeeOutputStream(System.out,baos);
        System.setOut(new PrintStream( teeOut));
        System.setErr(new PrintStream( teeErr));

        JdkLog2Log4jInit.jdk2log4j()
        Log4j1Utils.setLog4jAppender()
        Log4j2Utils.setLog4jAppender()

    }

    @Override
    protected void setUp() throws Exception {
        baos.reset()
        // JrrClassUtils.setFieldValue(baos,"buf",new byte[0])
    }


    void testJdkLogging(){
        String logmsg="jdk logger"
        Logger.getLogger(InvokactionFrameWorkTest.name).info(logmsg)
        assert baos.toString().contains(logmsg)
    }

    void testCommonsLogging(){
        String logmsg="123 logger"
        LogFactory.getLog(InvokactionFrameWorkTest).info(logmsg)
        assert baos.toString().contains(logmsg)
    }


    void testSl4jLogging(){
        String logmsg="123 logger"
        org.slf4j.LoggerFactory.getLogger(InvokactionFrameWorkTest).info(logmsg)
        assert baos.toString().contains(logmsg)
    }

    void testLog4j1(){
        String logmsg="log4j1 test"
        org.apache.log4j.Logger.getLogger(InvokactionFrameWorkTest).info(logmsg)
        assert baos.toString().contains(logmsg)
    }

    void testLog4j2(){
        String logmsg="log4j2 test"
        org.apache.log4j.Logger.getLogger(InvokactionFrameWorkTest).info(logmsg)
        assert baos.toString().contains(logmsg)
    }


    void testLocation(){
        println("Commons : "+UrlCLassLoaderUtils.getClassLocation(Log))
        println("sl4j : "+UrlCLassLoaderUtils.getClassLocation(org.slf4j.Logger))
        println("log4j1 : "+UrlCLassLoaderUtils.getClassLocation(org.apache.log4j.Logger))
        println("log4j2 : "+UrlCLassLoaderUtils.getClassLocation(org.apache.logging.log4j.Logger))
        println("logback : "+UrlCLassLoaderUtils.getClassLocation(ch.qos.logback.classic.Logger))
    }

}
