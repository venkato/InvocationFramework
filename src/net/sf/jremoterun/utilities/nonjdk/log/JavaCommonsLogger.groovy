package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.impl.LogFactoryImpl

@CompileStatic
public class JavaCommonsLogger {


    public
    static void setCommonsLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        LogFactory.releaseAll();
        org.apache.logging.log4j.jcl.LogFactoryImpl log4jLoggerLogFactory = new org.apache.logging.log4j.jcl.LogFactoryImpl();
        Log instance = log4jLoggerLogFactory.getInstance("test");
        JrrClassUtils.setFieldValue(LogFactory.class, "nullClassLoaderFactory", log4jLoggerLogFactory);
    }

    static setLoggerProps() {
        List<String> props = [
                LogFactoryImpl.LOG_PROPERTY,
                Log.getName(),
                "org.apache.commons.logging.log",
        ]

        String classname = org.apache.logging.log4j.jcl.Log4jLog.getName();
        props.each { System.setProperty(it, classname) }
    }


}
