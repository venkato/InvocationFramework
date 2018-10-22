package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum KafkaMavenIds implements MavenIdContains, EnumNameProvider, ToFileRef2 {
    connect__api,
    connect__basic__auth__extension,
    connect__file,
    connect__json,
    connect__runtime,
    connect__transforms,
//    kafka_2___11,
    kafka_2___12,
    kafka__clients,
    kafka__examples,
    kafka__log4j__appender,
    kafka__streams,
    kafka__streams__examples,
//    kafka__streams__scala_2___11,
    kafka__streams__scala_2___12,
    kafka__streams__test__utils,
    kafka__tools,

//    streams__quickstart__java,
//    streams__quickstart,
    ;


    MavenId m;

    KafkaMavenIds() {
        String artifactId = name().replace('___', '.')
        artifactId = artifactId .replace('__', '-')
        m = new MavenId('org.apache.kafka', artifactId, '2.2.1');
    }

    public static List<KafkaMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }


}
