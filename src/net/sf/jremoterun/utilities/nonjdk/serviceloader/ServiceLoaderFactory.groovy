package net.sf.jremoterun.utilities.nonjdk.serviceloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace

import java.lang.reflect.Field;
import java.util.logging.Logger;

@CompileStatic
class ServiceLoaderFactory extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
//    public static ServiceLoaderFactory instance = new ServiceLoaderFactory();
    public static Field serviceClassField = JrrClassUtils.findField(ServiceLoader, 'service');
    public ServiceLoaderStorage loaderStorage;

    ServiceLoaderFactory(ServiceLoaderStorage loaderStorage) {
        this.loaderStorage = loaderStorage
    }


    @Override
    Object get(Object key) {
        if (!(key instanceof ServiceLoader)) {
            throw new RuntimeException("Strange key ${key.getClass()} : ${key}");
        }
        ServiceLoader serviceLoader1 = key as ServiceLoader;
        return getImpl(serviceLoader1)
    }

    Iterator getImpl(ServiceLoader serviceLoader1) {
        Class serviceClass = serviceClassField.get(serviceLoader1)
        if (!loaderStorage.servicesTried.containsKey(serviceClass)) {
            loaderStorage.servicesTried.put(serviceClass, new JustStackTrace())
        }
        if (loaderStorage.customize.contains(serviceClass.getName())) {
            return createIterator(serviceLoader1, serviceClass)
        }
        return null
    }

    Iterator createIterator(ServiceLoader serviceLoader, Class service) {
        return new IteratorServiceLoader(serviceLoader, service, loaderStorage);
    }

    @Deprecated
    static void init() {
        ServiceLoaderStorage.init()

    }
}
