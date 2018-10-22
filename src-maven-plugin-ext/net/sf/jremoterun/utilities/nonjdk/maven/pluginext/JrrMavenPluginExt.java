package net.sf.jremoterun.utilities.nonjdk.maven.pluginext;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory;
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory;
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode;
import org.apache.maven.plugin.AbstractMojo;

import java.io.File;
import java.util.logging.Logger;

@CompileStatic
public class JrrMavenPluginExt extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static AbstractMojo abstractMojo;
    public static String insidePluginFileName="jrrInsidePluginFileName";
    public static File file;

    @Override
    public Object getImpl(Object key) throws Exception {
        log.info ("hello world2");
        abstractMojo = (AbstractMojo) key;

        //File f = " somefile\\configs_s\\addMaven.groovy" as File;
        if(file==null){
            String propertyValue = System.getProperty(insidePluginFileName);
            if(propertyValue==null){
                throw new Exception("Set prop : ${insidePluginFileName}");
            }
            file = new File(propertyValue);
        }
        RunnableFactory.createRunner(file).run();
        return null;
    }
}
