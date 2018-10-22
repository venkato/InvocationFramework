package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Okhttp3MavenIds implements MavenIdContains, ToFileRef2, EnumNameProvider {

    okhttp_urlconnection,
    okhttp_tls,
    okhttp_sse,
    logging_interceptor,
    okhttp_dnsoverhttps,
    okhttp_brotli,
    okhttp,
    okcurl,
    ;

    MavenId m;

    Okhttp3MavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId("com.squareup.okhttp3:${artifactId}:4.9.1");
    }

    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}
