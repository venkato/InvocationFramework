package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.apache.ivy.util.AbstractMessageLogger
import org.apache.ivy.util.DefaultMessageLogger
import org.apache.ivy.util.MessageLoggerEngine;

import java.util.logging.Logger;

@CompileStatic
class AddDefaultIgnoreClasses implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        addIgnoreClasses();
    }

    static void addAzureIgnoreClasses(){
        JrrClassUtils.ignoreClassesForCurrentClass.add('com.azure.core.util.logging.');
        JrrClassUtils.ignoreClassesForCurrentClass.add('com.microsoft.rest.interceptors.');
        addClRef(new ClRef('com.microsoft.rest.interceptors.LoggingInterceptor'));
        addClRef(new ClRef('com.microsoft.rest.retry.RetryHandler'));
        addClRef(new ClRef('com.microsoft.azure.credentials.AzureTokenCredentialsInterceptor'));
        addClRef(new ClRef('com.microsoft.azure.management.resources.fluentcore.utils.ResourceManagerThrottlingInterceptor'));
        addClRef(new ClRef('com.microsoft.azure.management.resources.fluentcore.utils.ProviderRegistrationInterceptor'));

    }

    static void addOkHttpIgnoreClasses(){
        addClRef(new ClRef('okhttp3.internal.http.RealInterceptorChain'));
        addClRef(new ClRef('okhttp3.internal.connection.ConnectInterceptor'));
        addClRef(new ClRef('okhttp3.internal.cache.CacheInterceptor'));
        addClRef(new ClRef('okhttp3.internal.http.BridgeInterceptor'));
        addClRef(new ClRef('okhttp3.internal.http.RetryAndFollowUpInterceptor'));
        addClRef(new ClRef('okhttp3.internal.connection.RealCall'));

    }

    static void addRetrofit(){
        addClRef(new ClRef('retrofit2.OkHttpCall'));
        addClRef(new ClRef('retrofit2.adapter.rxjava.CallExecuteOnSubscribe'));
        addClRef(new ClRef('rx.Observable'));
        addClRef(new ClRef('rx.internal.operators.OnSubscribeMap'));
        addClRef(new ClRef('rx.internal.operators.OnSubscribeLift'));
        addClRef(new ClRef('rx.observables.BlockingObservable'));
    }

    static void addIgnoreClasses(){
        addClRef(new ClRef('org.jetbrains.java.decompiler.IdeaLogger'));
        addClRef(new ClRef('reactor.util.Loggers'));
        addClRef(new ClRef('org.netbeans.lib.profiler.ProfilerLogger'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable.AlertTableWrapper'));
        addClRef(new ClRef('com.jcraft.jsch.JrrSchSessionLog'));
        addClRef(new ClRef('org.eclipse.core.internal.runtime.PlatformLogWriter'));
        addClRef(new ClRef('org.jboss.windup.decompiler.fernflower.FernflowerJDKLogger'));
        addClRef(new ClRef("org.apache.kafka.common.utils.LogContext"));
        addClRef(new ClRef("sun.net.www.protocol.http.HttpURLConnection"));
        addClRef(new ClRef("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection"));
        addClRef(new ClRef("java.net.SocksSocketImpl"));
        addClRef(new ClRef("org.sonatype.guice.bean.reflect.Logs"));
        addClRef(new ClRef("org.glassfish.jersey.logging.LoggingInterceptor"));
        addClRef(new ClRef("net.sf.jremoterun.utilities.nonjdk.git.GitProgressMonitorJrr"));

        JrrClassUtils.ignoreClassesForCurrentClass.add("org.apache.maven.cli.logging");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.apache.maven.monitor.logging");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.jdt.internal.junit");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.osgi.internal.log.");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.core.internal.runtime.Log.");
        JrrClassUtils.ignoreClassesForCurrentClass.add('io.netty.util.internal.logging.');
//        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j1Utils.getPackage().getName());
        addClRef(org.eclipse.jgit.lib.BatchingProgressMonitor);
        addClRef(org.eclipse.jgit.lib.ThreadSafeProgressMonitor);
        addClRef(net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler);
        addClRef(net.sf.jremoterun.utilities.nonjdk.log.JdkIntoLog4j2Converter);
        addClRef(net.sf.jremoterun.utilities.mdep.ivy.JrrIvyMessageLogger);
        addClRef(org.apache.ivy.util.DefaultMessageLogger);
        addClRef(org.apache.ivy.util.AbstractMessageLogger);
        addClRef(org.apache.ivy.util.MessageLoggerEngine);
        addClRef(org.apache.ivy.util.Message);
        addOkHttpIgnoreClasses()
        //added for azure http headers logs
        addAzureIgnoreClasses()
        addRetrofit()
    }


    static void addClRef(Class clRef){
        JrrClassUtils.ignoreClassesForCurrentClass.add(clRef.getName());
    }

    static void addClRef(ClRef clRef){
        JrrClassUtils.ignoreClassesForCurrentClass.add(clRef.className);
    }

}
