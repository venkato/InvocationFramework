package net.sf.jremoterun.utilities.nonjdk.maven.pluginext

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import org.apache.maven.plugin.AbstractMojo;

import java.util.logging.Logger;

@CompileStatic
class JrrMavenPluginExt extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static AbstractMojo abstractMojo;

    @Override
    Object getImpl(Object key) throws Exception {
        log.info "hello world2";
        abstractMojo = key as AbstractMojo;
        File f = "F:\\nik\\configs_s\\addMaven.groovy" as File;
        RunnableFactory.createRunner(f).run()
        return null;
    }
}
