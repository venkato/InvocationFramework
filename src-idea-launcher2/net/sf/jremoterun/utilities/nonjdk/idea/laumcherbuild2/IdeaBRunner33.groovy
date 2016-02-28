package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.LauncherImpl

import java.util.logging.Logger

@CompileStatic
class IdeaBRunner33 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static enum A implements ClRefRef {
        addMavenIds(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.AddMavenIds')),
        ivyDepSetter(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.IvyDepResolverSetter')),
        redir(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.Redirector')),
        runnerImpl(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaBRunner34'))
        ;

        ClRef clRef;

        A(ClRef clRef) { this.clRef = clRef }
    }


    @Override
    void run() {
        f1()
    }


    void f1() {
        log.info "loading framework2"
        RunnableFactory.runRunner A.addMavenIds
        RunnableFactory.runRunner A.ivyDepSetter
        RunnableFactory.runRunner A.redir
        log.info "redirector set 2"
        long delay = System.currentTimeMillis() - LauncherImpl.startDate.getTime()
        log.info "startup delay : ${delay / 1000} s"
        RunnableFactory.runRunner A.runnerImpl

    }
}
