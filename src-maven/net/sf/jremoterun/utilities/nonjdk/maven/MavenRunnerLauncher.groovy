package net.sf.jremoterun.utilities.nonjdk.maven

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2
import org.codehaus.plexus.classworlds.launcher.Launcher
import org.codehaus.plexus.classworlds.realm.ClassRealm
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException

import java.lang.reflect.InvocationTargetException
import java.util.logging.Logger

@CompileStatic
class MavenRunnerLauncher {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef mavenRunner = new ClRef('net.sf.jremoterun.utilities.nonjdk.maven.MavenRunner')

    static void runMaven(File mavenBaseDir) {
        GroovyMethodRunnerParams.gmrp.addFilesToClassLoader.addAllJarsInDir mavenBaseDir.child('boot/')
        File m2Config = mavenBaseDir.child('bin/m2.conf')
        assert m2Config.exists()
        System.setProperty('classworlds.conf', m2Config.getAbsolutePath())
        System.setProperty('maven.home', mavenBaseDir.getAbsolutePath())
        File jasnsiNative = mavenBaseDir.child('lib/jansi-native/')
        assert jasnsiNative.exists()
        System.setProperty('library.jansi.path', jasnsiNative.getAbsolutePath())
        File userDir = new File(System.getProperty("user.dir"));
        assert userDir.exists()
        userDir = userDir.getCanonicalFile().getAbsoluteFile()
        System.setProperty('maven.multiModuleProjectDirectory', userDir.getAbsolutePath())
        GroovyMethodRunnerParams2.gmrp2.mainClass = mavenRunner
    }

}
