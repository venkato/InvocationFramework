package net.sf.jremoterun.utilities.nonjdk.serviceloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class IteratorServiceLoader implements Iterator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ServiceLoader serviceLoader;
    public Class service;
    public ClassLoader serviceClassLoader;
//    public Iterator originIterator;
    public Enumeration<URL> configs;
    public List<String> servicesAsStr = [];
    public Iterator<String> servicesAsIter;
    public ServiceLoaderStorage serviceLoaderFactory;
    public LinkedHashMap<String, Object> providers = new LinkedHashMap<>();
    public volatile String next12

    IteratorServiceLoader(ServiceLoader serviceLoader, Class service, ServiceLoaderStorage serviceLoaderFactory) {
        this.serviceLoader = serviceLoader
        this.serviceLoaderFactory = serviceLoaderFactory
        this.service = service;
        serviceClassLoader = JrrClassUtils.getFieldValue(serviceLoader, 'loader') as ClassLoader
        String fullName = "META-INF/services/" + service.getName();
        if (serviceClassLoader == null) {
            configs = ClassLoader.getSystemResources(fullName);
        } else {
            configs = serviceClassLoader.getResources(fullName);
        }
        configs.toList().each {
            servicesAsStr.addAll(parseUrl(it));
        }
        servicesAsStr = servicesAsStr.unique()
        servicesAsStr.removeAll(serviceLoaderFactory.ignoreImpl2)
        servicesAsIter = servicesAsStr.iterator()
    }


//    @Override
//    boolean hasNext() {
//        while (true) {
//            boolean hasNext12 = servicesAsIter.hasNext()
//            if (!hasNext12) {
//                return false
//            }
//            String impl = servicesAsIter.next()
//            if (isOkImpl(impl)) {
//                next12 = impl
//                return true
//            }
//        }
//    }

//    boolean isOkImpl(String implClassName) {
//        Collection<String> skips = serviceLoaderFactory.skipImpl2
//        return !skips.contains(implClassName)
//    }
    @Override
    boolean hasNext() {
        return servicesAsIter.hasNext()
    }

    @Override
    Object next() {
        String string1 = servicesAsIter.next()
//        String string1 = next12
        return createService(string1)
    }

    private Object createService(String cn) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(cn, false, serviceClassLoader);
        } catch (ClassNotFoundException x) {
            fail(service, "Provider " + cn + " not found");
        }
        if (!service.isAssignableFrom(clazz)) {
            fail(service, "Provider " + cn + " not a subtype");
        }
        try {
            Object p = service.cast(clazz.newInstance());
            providers.put(cn, p);
            return p;
        } catch (Throwable x) {
            fail(service, "Provider " + cn + " could not be instantiated", x);
        }
        throw new Error();          // This cannot happen
    }

    List<String> parseUrl(URL u) throws ServiceConfigurationError {
        InputStream inn = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            inn = u.openStream();
            r = new BufferedReader(new InputStreamReader(inn, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0);
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) r.close();
                if (inn != null) inn.close();
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return names;
    }

    int parseLine(Class<?> service, URL u, BufferedReader r, int lc, List<String> names)
            throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                fail(service, u, lc, "Illegal configuration-file syntax: ${ln}");
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
//                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
//                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            if (!providers.containsKey(ln) && !names.contains(ln))
                names.add(ln);
        }
        return lc + 1;
    }

    private static void fail(Class<?> service, URL u, int line, String msg) throws ServiceConfigurationError {
        fail(service, "${u}:${line}:${msg}");
    }


    private static void fail(Class<?> service, String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, String msg, Throwable cause) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg, cause);
    }
}
