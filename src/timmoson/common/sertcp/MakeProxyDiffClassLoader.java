package timmoson.common.sertcp;

import javassist.util.proxy.Proxy;
import junit.framework.Assert;
import net.sf.jremoterun.ClassFinder;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.URLClassLoaderExt;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javassist.JavassistProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.client.ProxyCallDiffClassloader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.WeakHashMap;

public class MakeProxyDiffClassLoader {
	private static final Log log = LogFactory.getLog(Reflection.getCallerClass(1));

	public static <T> T makeClient(Class<T> class1) throws Exception {
		return makeClient(class1, class1.getName());
	}

	public static Object findSession() throws Exception {
		Object callInfoServer = IfSharedObjects.getCallInfo().get();
		if (callInfoServer == null) {
			throw new Exception("Can't find who call me");
		}
		Object session = JrrClassUtils.invokeJavaMethod(callInfoServer, "getSession");
		return session;
	}

	public static <T> T makeClient(Class<T> class1, String serviceId) throws Exception {
		Object session = findSession();
		T t = (T) JrrClassUtils.invokeJavaMethod(session, "makeClient", class1, serviceId);
		return t;
	}

	public static WeakHashMap<ClassLoader, WeakReference<URLClassLoaderExt>> classLoaderMap = new WeakHashMap();

	public static ClassLoader classloaderForClassess = ClassLoader.getSystemClassLoader();

	public static <T> T makeProxyForFromDiffClassLoader(Object fromObject, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		final URLClassLoaderExt classLoader2 = findClassLoader(clazz.getClassLoader());

		{
			try {
				Class<?> loadClass = classLoader2.loadClass(ProxyCallDiffClassloader.class.getName());
			} catch (ClassNotFoundException e) {
				try {
					log.info("defining ProxyCallDiffClassloader class");
					classLoader2.defineClassPublic(JrrUtils.convertClassToBytes(ProxyCallDiffClassloader.class));
				} catch (IOException e1) {
					log.warn(null, e1);
					throw new Error(e1);
				}
			}

		}

		ProxyCallInvocationDiffClassloader proxyCallInvocation = new ProxyCallInvocationDiffClassloader(fromObject);

		JavassistProxyFactory f = new JavassistProxyFactory(proxyCallInvocation, true, classLoader2, clazz,
				ProxyCallDiffClassloader.class);
		return (T) f.createInstance();
	}

	public static URLClassLoaderExt findClassLoader(ClassLoader classLoader) {
		// log.info(classLoader);
		if (classLoader == null) {
			classLoader = classloaderForClassess;
		}
		// if (classLoader instanceof URLClassLoaderExt) {
		// URLClassLoaderExt new_name = (URLClassLoaderExt) classLoader;
		// return new_name;
		// }
		WeakReference<URLClassLoaderExt> weakRef = classLoaderMap.get(classLoader);
		URLClassLoaderExt urlClassLoaderExt = null;
		if (weakRef != null) {
			urlClassLoaderExt = weakRef.get();
		}
		if (urlClassLoaderExt == null) {
			// log.info("3322");
			urlClassLoaderExt = new URLClassLoaderExt(new URL[0], classLoader);
			urlClassLoaderExt.setClassFinder(new ClassFinder() {
				@Override
				public Class loadClass(URLClassLoaderExt classLoaderExt2, String name, boolean resolve)
						throws ClassNotFoundException {
					// log.info(name);
					Class class1;
					if (name.startsWith("javassist.")) {
						class1 = Proxy.class.getClassLoader().loadClass(name);
						Assert.assertEquals(class1.getClassLoader(), Proxy.class.getClassLoader());
						// log.info(class1.getName());
						return class1;
					} else {
						class1 = super.loadClass(classLoaderExt2, name, resolve);
					}
					// log.info(name + " " + class1.getClassLoader());
					return class1;
				}
			});
			classLoaderMap.put(classLoader, new WeakReference<URLClassLoaderExt>(urlClassLoaderExt));
		} else {
			// log.info("not null");
		}
		return urlClassLoaderExt;
	}
}
