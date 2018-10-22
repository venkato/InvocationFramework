package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum NettyMavenIds implements MavenIdContains , EnumNameProvider, ToFileRef2{


    transport_native_unix_common,
    transport_native_kqueue,
    microbench,
    testsuite_http2,
    testsuite_autobahn,
    transport_native_unix_common_tests,
    codec_smtp,
    codec_redis,
    dev_tools,
    all,
    transport_native_epoll,
    testsuite_osgi,
    testsuite,
    example,
    handler_proxy,
    transport_udt,
    transport_sctp,
    transport_rxtx,
    resolver_dns,
    codec_xml,
    codec_stomp,
    codec_socks,
    codec_mqtt,
    codec_memcache,
    codec_http2,
    codec_http,
    handler,
    codec_haproxy,
    codec_dns,
    codec,
    transport,
    resolver,
    buffer,
    common
    ;


    MavenId m;

    NettyMavenIds() {
        m = new MavenId('io.netty','netty-'+ name().replace('_', '-'), '4.1.31.Final');
    }

    public static List<NettyMavenIds> allEnums = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }
}
