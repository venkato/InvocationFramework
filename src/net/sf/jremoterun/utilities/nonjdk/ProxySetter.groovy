package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ProxySetter extends ProxySelector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<java.net.Proxy> noProxy = [Proxy.NO_PROXY];
    public static List<java.net.Proxy> defaultProxy;

    public static String nonProxyHosts = 'nonProxyHosts'

    public static String proxyHost = 'proxyHost'
    public static String proxyPort = 'proxyPort'
    public static String proxyUser = 'proxyUser'
    public static String proxyUserName = 'proxyUserName'
    public static String proxyPassword = 'proxyPassword'
    public static String proxySet = 'proxySet'
    public static List<String> prefix = ['http', 'https', 'ftp',]

    Set<String> noProxyConcreatHosts = new HashSet<>()
    Set<String> noProxyPatternHosts = new HashSet<>()

    void addConcreatHostAndIp(String host) {
        long time = System.currentTimeMillis()
        InetAddress address = InetAddress.getByName(host)
        String name = address.hostName
        String address1 = address.address
        long takeTime = System.currentTimeMillis() - time
        if (takeTime > 1000) {
            log.info "resolving ${host} took ${takeTime / 1000} sec"
        }
        noProxyConcreatHosts.add(name)
        noProxyConcreatHosts.add(address1)
    }


    void setProxySelector3(String host, int port,String user, String password) {
        setProxy(host,port)
        setAuth(user,password)
        setProxySelector2()
    }


    void setProxySelector2() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        if (defaultSel == null) {
            List<String> joins = []
            joins.addAll noProxyConcreatHosts
            joins.addAll noProxyPatternHosts.collect { '*.' + it }
            setNoProxy(joins)
            ProxySelector.setDefault(this)
        } else {
            throw new Exception("Proxy selector already set : ${defaultSel.class.name} ${defaultSel}")
        }
    }

    @Override
    List<java.net.Proxy> select(URI uri) {
        final boolean useProxy
        if (defaultProxy == null) {
            useProxy = false
        } else {
            String host = uri.host
            if (noProxyConcreatHosts.contains(host)) {
                useProxy = false
            } else {
                String noProxyPrefix = noProxyPatternHosts.find { host.endsWith(it) }
                if (noProxyPrefix == null) {
                    useProxy = true
                } else {
                    useProxy = false
                }
            }
        }
        log.info "connecting to ${uri} ${useProxy}"
        if(useProxy){
            return defaultProxy
        }
        return noProxy
    }



    void setProxy(String host, int port) {
        setProperty(proxyHost, host)
        setProperty(proxyPort, "${port}")

        InetSocketAddress proxy = new InetSocketAddress(host, port)
        java.net.Proxy proxy2 = new Proxy(java.net.Proxy.Type.HTTP, proxy)
        defaultProxy = [proxy2]
        System.setProperty(proxySet, 'true')


    }

    static void setProxySelectorWithJustLogging() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        if (defaultSel == null) {
            ProxySelector.setDefault(new ProxySetter())
        } else {
            log.info "Proxy selector already set : ${defaultSel.class.name} ${defaultSel}"
        }
    }


    static void setAuth(String user, String password) {
        Authenticator initialAuth = JrrClassUtils.getFieldValue(Authenticator, "theAuthenticator") as Authenticator
        if (initialAuth == null) {
            ProxyAuth auth = new ProxyAuth(user, password)
            Authenticator.setDefault(auth)
            setProperty(proxyUser, user)
            setProperty(proxyUserName, user)
            setProperty(proxyPassword, password)
        } else {
            log.info "proxy auth alredy set ${initialAuth.class.name}"
        }

    }


    static void setProperty(String name, String value) {
        prefix.each {
            System.setProperty("${it}.${name}", value)
            System.setProperty("${it}.${name}", value)
        }
    }

    static void setNoProxy(List<String> noProxy) {
        setProperty(nonProxyHosts, noProxy.join('|'))
    }


    @Override
    void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.info "connection failed to ${uri}"
    }

}
