package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class GradleMavenIds {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<MavenId> gradleMavenIds = [
//            new MavenId('net.rubygrapefruit:native-platform-freebsd-amd64-libcpp:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-freebsd-amd64-libstdcpp:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-freebsd-i386-libcpp:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-freebsd-i386-libstdcpp:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-amd64:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-amd64-ncurses5:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-amd64-ncurses6:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-i386:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-i386-ncurses5:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-linux-i386-ncurses6:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-osx-amd64:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-osx-i386:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-windows-amd64:0.14'),
//            new MavenId('net.rubygrapefruit:native-platform-windows-i386:0.14'),
            new MavenId('net.rubygrapefruit:native-platform:0.14'),
            new MavenId('org.gradle:gradle-base-services:4.3.1'),
            new MavenId('org.gradle:gradle-base-services-groovy:4.3.1'),
            new MavenId('org.gradle:gradle-core:4.3.1'),
            new MavenId('org.gradle:gradle-dependency-management:4.3.1'),
            new MavenId('org.gradle:gradle-logging:4.3.1'),
            new MavenId('org.gradle:gradle-messaging:4.3.1'),
            new MavenId('org.gradle:gradle-model-core:4.3.1'),
            new MavenId('org.gradle:gradle-process-services:4.3.1'),
            new MavenId('org.gradle:gradle-resources:4.3.1'),
            new MavenId('org.gradle:gradle-tooling-api:4.3.1'),
            new MavenId('org.gradle:gradle-workers:4.3.1'),
            new MavenId('org.gradle:gradle-wrapper:4.3.1'),
    ]

}
