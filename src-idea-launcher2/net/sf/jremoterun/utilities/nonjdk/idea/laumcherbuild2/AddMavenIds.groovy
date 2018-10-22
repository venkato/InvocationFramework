package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class AddMavenIds implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        f1()
    }





    void f1() {
        IdeaBRunner21.adder.add CustObjMavenIds.commonsIo
        IdeaBRunner21.adder.addAll DropshipClasspath.allLibsWithoutGroovy
    }
}
