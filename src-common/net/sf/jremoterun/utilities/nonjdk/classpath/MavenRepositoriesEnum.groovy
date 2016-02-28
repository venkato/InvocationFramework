package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository

@CompileStatic
enum MavenRepositoriesEnum implements IBiblioRepository{

// https://oss.sonatype.org/#view-repositories
    groovy('https://dl.bintray.com/groovy/maven'),
    jcenter('https://jcenter.bintray.com'),
    central('https://repo1.maven.org/maven2'),
    gradle('https://repo.gradle.org/gradle/libs-releases-local'),
    androidGoogle('https://dl.google.com/dl/android/maven2'),
    javassh('http://artifactory.javassh.com/public-releases'),
    eclipse('https://repo.eclipse.org/content/groups/releases'),
    jetbrainsIdea('https://www.jetbrains.com/intellij-repository/releases'),
    sonatypeRelease('https://oss.sonatype.org/service/local/repositories/releases'),
    ;

    String url;

    MavenRepositoriesEnum(String url) {
        this.url = url
    }


}
