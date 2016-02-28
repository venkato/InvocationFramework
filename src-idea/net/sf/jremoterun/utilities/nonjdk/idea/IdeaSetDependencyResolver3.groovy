package net.sf.jremoterun.utilities.nonjdk.idea;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2;

import java.util.logging.Logger;

@CompileStatic
public class IdeaSetDependencyResolver3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static void setDepResolver() throws Exception {
        try {
            IvyDepResolver2.setDepResolver();
        }catch (Throwable e){
            log.info("set maven sep failed");
            throw new Exception("set maven sep failed",e);
        }
    }



}
