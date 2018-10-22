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

    static void addClRef(ClRef clRef){
        JrrClassUtils.ignoreClassesForCurrentClass.add(clRef.className);
    }

    static void addIgnoreClasses(){
        addClRef(new ClRef('reactor.util.Loggers'));
        addClRef(new ClRef('org.netbeans.lib.profiler.ProfilerLogger'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable.AlertTableWrapper'));
        JrrClassUtils.ignoreClassesForCurrentClass.add('io.netty.util.internal.logging.');
//        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j1Utils.getPackage().getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(net.sf.jremoterun.utilities.nonjdk.log.JdkIntoLog4j2Converter.getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(net.sf.jremoterun.utilities.mdep.ivy.JrrIvyMessageLogger.getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(org.apache.ivy.util.DefaultMessageLogger.getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(org.apache.ivy.util.AbstractMessageLogger.getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(org.apache.ivy.util.MessageLoggerEngine.getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(org.apache.ivy.util.Message.getName());
        addOkHttpIgnoreClasses()
        //added for azure http headers logs
        addAzureIgnoreClasses()
        addRetrofit()
    }



}
