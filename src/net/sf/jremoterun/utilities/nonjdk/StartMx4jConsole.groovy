package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import mx4j.tools.adaptor.http.HttpAdaptor
import mx4j.tools.adaptor.http.XSLTProcessor
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.management.MBeanServer
import javax.management.ObjectName
import java.util.logging.Logger

@CompileStatic
class StartMx4jConsole {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static void startJmxConsole(int port) throws Exception {
        startJmxConsole(port,JrrUtils.findLocalMBeanServer())
    }


    public static void startJmxConsole(int port,MBeanServer exportBeanServer) throws Exception {
        final ObjectName httpAdapterON = JrrUtils.createObjectName("mx4j:type=HttpAdaptor");
        final ObjectName xsltProcessorON = JrrUtils.createObjectName("mx4j:type=XSLTProcessor");
        ServerSocket freePort = new ServerSocket(port);
        HttpAdaptor httpAdaptor = new HttpAdaptor(){
            @Override
            void preDeregister() throws Exception {
//                super.preDeregister()
            }
        };
        XSLTProcessor processor = new XSLTProcessor();
        httpAdaptor.setProcessor(processor);
        httpAdaptor.setHost("0.0.0.0");
//        JrrUtils.findLocalMBeanServer().registerMBean(httpAdaptor, httpAdapterON);
//        JrrUtils.findLocalMBeanServer().registerMBean(processor, xsltProcessorON);
        exportBeanServer.registerMBean(httpAdaptor, httpAdapterON);
        exportBeanServer.registerMBean(processor, xsltProcessorON);
        freePort.close();
        httpAdaptor.setPort(freePort.getLocalPort());
        httpAdaptor.start();

    }

}
