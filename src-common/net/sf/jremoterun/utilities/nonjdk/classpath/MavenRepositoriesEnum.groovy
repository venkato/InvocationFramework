package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository

@CompileStatic
enum MavenRepositoriesEnum implements IBiblioRepository{

// https://oss.sonatype.org/#view-repositories

    jcenter('https://jcenter.bintray.com'),
    central('https://repo1.maven.org/maven2'),
    gradle('https://repo.gradle.org/gradle/libs-releases-local'),
    gradlePlugins('https://plugins.gradle.org/m2'),
    androidGoogle('https://dl.google.com/dl/android/maven2'),// https://dl.google.com/dl/android/maven2/index.html
    googleMirror('https://maven-central.storage-download.googleapis.com/repos/central/data/'),
    javassh('http://artifactory.javassh.com/public-releases'),
    eclipse('https://repo.eclipse.org/content/groups/releases'),
    jetbrainsIdea('https://www.jetbrains.com/intellij-repository/releases'),
    sonatypeRelease(  'https://oss.sonatype.org/service/local/repositories/releases'),
    sonatypeSnapshots('https://oss.sonatype.org/content/repositories/snapshots/'),
    spring('https://repo.spring.io/plugins-release'),
    mozilla('https://maven.mozilla.org/maven2'),
    groovy('https://dl.bintray.com/groovy/maven'),
    teamcityRestClient('https://dl.bintray.com/jetbrains/teamcity-rest-client'),
    teamcity('https://download.jetbrains.com/teamcity-repository'),
    ;

    String url;

    MavenRepositoriesEnum(String url) {
        this.url = url
    }


}
