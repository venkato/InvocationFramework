package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.slf4j.impl.StaticLoggerBinder

import java.util.logging.Logger

@CompileStatic
class Sl4jLoggerCommon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void setStatusInited() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        JrrClassUtils.setFieldValue(org.slf4j.LoggerFactory, 'INITIALIZATION_STATE', 3);
    }

    static void setLoggerImpl(org.slf4j.ILoggerFactory factoryImpl) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
        org.slf4j.Logger instance = factoryImpl.getLogger("test");
        JrrClassUtils.setFieldValue(singleton, "loggerFactory", factoryImpl);
    }
}
