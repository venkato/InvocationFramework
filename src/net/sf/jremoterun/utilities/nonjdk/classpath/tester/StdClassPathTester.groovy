package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import com.sun.jna.Native
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2Utils
import org.apache.log4j.Level
import org.apache.logging.log4j.LogManager
import org.slf4j.LoggerFactory
import sun.jvmstat.monitor.HostIdentifier

import java.util.logging.Logger

@CompileStatic
class StdClassPathTester extends ClassPathTesterHelper {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    public void runImpl() throws Exception {
        CheckNonCache2.check();


        checkClassOnce(Level, LatestMavenIds.log4jOld)
        checkClassOnce(org.apache.commons.logging.LogFactory, CustObjMavenIds.commonsLoggingMavenId)

        checkClassOnce(org.slf4j.LoggerFactory, CustObjMavenIds.slf4jApi)
        checkClassOnce(org.slf4j.Logger, CustObjMavenIds.slf4jApi)



        ClassPathTesterHelper.checkClassOnce(com.google.common.net.HostAndPort, LatestMavenIds.guavaMavenId)
//        checkClassOnce(com.jidesoft.swing.Gripper, new MavenId("com.jidesoft:jide-oss:3.6.18"))

        checkClassOnce(Native, LatestMavenIds.jna)
//        checkClassOnce(org.jsoup.select.Elements, new MavenId('org.jsoup:jsoup:1.11.2'))
//        checkClassOnce(jcifs.Config, new MavenId("jcifs:jcifs:1.3.17"))
//        checkClassOnce(org.apache.http.auth.AuthScheme, new MavenId("org.apache.httpcomponents:httpclient:4.5.3"))

//        checkClassOnce(org.apache.xerces.dom.AttributeMap, new MavenId("xerces:xercesImpl:2.11.0"))


        org.apache.log4j.Logger logger1 = org.apache.log4j.Logger.getLogger("test");
        checkClassInstanceOf(logger1, org.apache.log4j.Logger.class);

//        System.setProperty(LogManager.FACTORY_PROPERTY_NAME, Log4jContextFactory.class.getName());
        Log4j2Utils.checkAndFixFactory();
        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger("test");
        checkClassInstanceOf(logger2, org.apache.logging.log4j.core.Logger.class);

        org.slf4j.Logger loggerSl4j = LoggerFactory.getLogger("test");
        // checkClassInstanceOf(loggerSl4j, org.apache.logging.slf4j.Log4jLogger.class);

        checkFieldExists(Level, 'TRACE')

        // getHostText used in jedit ssh
        com.google.common.net.HostAndPort.fromString('127.0.0.1:5223').getHostText();

        URL jdkLoggerExtentionMethods = JrrClassUtils.currentClassLoader.getResource("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule")
        assert jdkLoggerExtentionMethods != null
        checkToolsJar()

        assert MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null
        IvyDepResolver2.setDepResolver()
        DropshipClasspath.downloadyIvydepToIvyDir()
    }


    static void checkToolsJar() {
//        Class hostId =
        assert HostIdentifier.classLoader == StdClassPathTester.classLoader
        checkClassOnce(HostIdentifier, ClassPathTesterHelper.mavenCommonUtils.getToolsJarFile())
    }


}