package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;

@CompileStatic
interface GitlabLibsMavenIds {

//    new MavenId('com.fasterxml.jackson.core:jackson-annotations:2.12.2'),
//    new MavenId('com.fasterxml.jackson.core:jackson-core:2.12.2'),
//    new MavenId('com.fasterxml.jackson.core:jackson-databind:2.12.2'),
//    new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.12.2'),
//    new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.12.2'),
//    new MavenId('com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.12.2'),
//            new MavenId('commons-codec:commons-codec:1.11'),
//            new MavenId('commons-logging:commons-logging:1.2'),
//    new MavenId('jakarta.activation:jakarta.activation-api:1.2.2'),
//    new MavenId('jakarta.annotation:jakarta.annotation-api:1.3.5'),
//    new MavenId('jakarta.servlet:jakarta.servlet-api:4.0.3'),
//            new MavenId('jakarta.ws.rs:jakarta.ws.rs-api:2.1.6'),
//    new MavenId('jakarta.xml.bind:jakarta.xml.bind-api:2.3.2'),
//            new MavenId('org.apache.httpcomponents:httpclient:4.5.9'),
//            new MavenId('org.apache.httpcomponents:httpcore:4.4.11'),
//            new MavenId('org.gitlab4j:gitlab4j-api:4.15.7'),
//    new MavenId('org.glassfish.hk2.external:aopalliance-repackaged:2.6.1'),
//    new MavenId('org.glassfish.hk2.external:jakarta.inject:2.6.1'),
//    new MavenId('org.glassfish.hk2:hk2-api:2.6.1'),
//    new MavenId('org.glassfish.hk2:hk2-locator:2.6.1'),
//    new MavenId('org.glassfish.hk2:hk2-utils:2.6.1'),
//    new MavenId('org.glassfish.hk2:osgi-resource-locator:1.0.3'),
//    new MavenId('org.glassfish.jersey.connectors:jersey-apache-connector:2.30.1'),
//    new MavenId('org.glassfish.jersey.core:jersey-client:2.30.1'),
//    new MavenId('org.glassfish.jersey.core:jersey-common:2.30.1'),
//    new MavenId('org.glassfish.jersey.inject:jersey-hk2:2.30.1'),
//    new MavenId('org.glassfish.jersey.media:jersey-media-multipart:2.30.1'),
//    new MavenId('org.jvnet.mimepull:mimepull:1.9.11'),

    MavenId coreLib = new MavenId('org.gitlab4j:gitlab4j-api:4.17.0');

    List<MavenId> fastXmlsOthers = [
            new MavenId('com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.2'),
            new MavenId('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.2'),
            new MavenId('com.fasterxml.jackson.datatype:jackson-datatype-joda:2.12.2'),
            new MavenId('com.fasterxml.woodstox:woodstox-core:6.0.1'),
    ]

    List<MavenId> fastXmls = [
            new MavenId('com.fasterxml.jackson.core:jackson-annotations:2.12.2'),
            new MavenId('com.fasterxml.jackson.core:jackson-core:2.12.2'),
            new MavenId('com.fasterxml.jackson.core:jackson-databind:2.12.2'),
            new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.12.2'),
            new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.12.2'),
            new MavenId('com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.12.2'),
    ];


    List<MavenId> glassfish = [
            new MavenId('org.glassfish.hk2.external:aopalliance-repackaged:2.6.1'),
            new MavenId('org.glassfish.hk2.external:jakarta.inject:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-api:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-locator:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-utils:2.6.1'),
            new MavenId('org.glassfish.hk2:osgi-resource-locator:1.0.3'),
            new MavenId('org.glassfish.jersey.connectors:jersey-apache-connector:2.30.1'),
            new MavenId('org.glassfish.jersey.core:jersey-client:2.30.1'),
            new MavenId('org.glassfish.jersey.core:jersey-common:2.30.1'),
            new MavenId('org.glassfish.jersey.inject:jersey-hk2:2.30.1'),
            new MavenId('org.glassfish.jersey.media:jersey-media-multipart:2.30.1'),
            new MavenId('org.jvnet.mimepull:mimepull:1.9.11'),
    ];


    MavenId jakartaWsRsApi = new MavenId('jakarta.ws.rs:jakarta.ws.rs-api:2.1.6');

    List<MavenId> jakarta = [
            new MavenId('jakarta.activation:jakarta.activation-api:1.2.2'),
            new MavenId('jakarta.annotation:jakarta.annotation-api:1.3.5'),
            new MavenId('jakarta.servlet:jakarta.servlet-api:4.0.3'),
            jakartaWsRsApi,
            new MavenId('jakarta.xml.bind:jakarta.xml.bind-api:2.3.2'),
    ];

}
