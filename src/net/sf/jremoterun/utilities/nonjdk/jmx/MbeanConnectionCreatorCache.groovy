package net.sf.jremoterun.utilities.nonjdk.jmx

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator

import javax.management.ObjectName;
import java.util.logging.Logger;

@CompileStatic
class MbeanConnectionCreatorCache {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Object lock = new Object()
    public static Map<String, MbeanConnectionCreator> port2connectoin = [:];
    public static Map<String, Object> class2connection = [:];

    static <T> T getClient(Class<T> clazz, String host, int port, ObjectName objectName) {
        String key = clazz.getName() + ':' + host + ':' + port+':'+objectName;
        Object obj = class2connection.get(key)
        if (obj == null) {
            synchronized (lock) {
                obj = port2connectoin.get(key)
                if (obj == null) {
                    MbeanConnectionCreator connection = getConnection(host, port)
                    obj = MBeanClient.buildMbeanClient(clazz, connection, objectName);
                    if (!clazz.isInstance(obj)) {
                        throw new ClassCastException("Failed cast ${obj.getClass()} to ${clazz.getName()}")
                    }
                    class2connection.put(key, obj) as T
                }
            }
        }
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException("Failed cast ${obj.getClass()} to ${clazz.getName()}")
        }
        return obj as T
    }

    static MbeanConnectionCreator getConnection(String host, int port) {
        String key = host + ':' + port
        MbeanConnectionCreator get1 = port2connectoin.get(key)
        if (get1 == null) {
            synchronized (lock) {
                get1 = port2connectoin.get(key)
                if (get1 == null) {
                    get1 = new MbeanConnectionCreator(key);
                    port2connectoin.put(key, get1)
                }
            }
        }
        return get1;
    }

    static MbeanConnectionCreator getLocalConnection(int port) {
        return getConnection('127.0.0.1', port);
    }

}
