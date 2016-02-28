package net.sf.jremoterun.utilities.nonjdk.helfyutils

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class HelpfyRegister implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        registerMbean()
    }


    static void registerMbean(){
        boolean registred =JrrUtils.findLocalMBeanServer().isRegistered(HelpfyThreadBean.objectName)
        if(registred){
            log.info "already registerd ${HelpfyThreadBean.objectName}"
        }else {
            MBeanFromJavaBean.registerMBean(new HelpfyThreadBean())
        }
    }
}
