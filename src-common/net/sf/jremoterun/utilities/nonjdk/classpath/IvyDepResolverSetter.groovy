package net.sf.jremoterun.utilities.nonjdk.classpath;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class IvyDepResolverSetter implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    void run() {
        IvyDepResolver2.setDepResolver()
    }
}
