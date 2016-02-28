package net.sf.jremoterun.utilities.jrrbean;

import java.beans.IntrospectionException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.MBeanFromJavaBean;
import net.sf.jremoterun.utilities.javassist.DefaultMethodFilter;
import net.sf.jremoterun.utilities.javassist.JavassistProxyFactory;

/**
 * This class provides framework for creating wrapper around java bean. Values
 * of properties are shared thru different class loaders. Java bean should be
 * created and registered in storage using methods
 * {@link #makeBeanAndRegisterMBeanNoEx(Class)}. When you request bean property,
 * class return object from cache, if object exists in cache. If object not
 * exists in cache, framework request getter method and return it. If getter
 * return null, framework save null object in cache. Getter will invoked on last
 * created bean (e.g. with last classloader). You can set value using setter
 * method. You can add properties to bean, framework will share values of they
 * also. Values are stored in fields of bean. When new implementation is created
 * fields are copied.
 */
public class JrrBeanMaker {

	private static final Logger log = Logger.getLogger(JrrBeanMaker.class.getName());

	public static String jrrMBeansPrefix = "jrrbeans:type=";

	public static Method findClassMethod;

	public static String keyInViewer = JrrBeanMaker.class.getName();

	/**
	 * Value of field is the same in different classloaders.
	 */
	public static WeakHashMap<Class, WeakReference> cacheProxyBeans = new WeakHashMap();

	public static <T> T makeOrUpdateBeanAndRegisterMBeanNoEx(final URLClassLoader classLoader, final Class<T> clazz)
			throws Exception {
		Class<T> newClass;
		if (clazz.getClassLoader() == classLoader) {
			newClass = clazz;
		} else {
			newClass = (Class) classLoader.loadClass(clazz.getName());
			if (newClass.getClassLoader() == classLoader) {
			} else {
				if (findClassMethod == null) {
					findClassMethod = URLClassLoader.class.getDeclaredMethod("findClass", String.class);
					findClassMethod.setAccessible(true);
				}
				newClass = (Class) findClassMethod.invoke(classLoader, clazz.getName());
			}
		}
		return makeBeanAndRegisterMBean(newClass);
	}

	public static <T> T makeBeanAndRegisterMBeanNoEx(final Class<T> clazz) {
		try {
			return makeBeanAndRegisterMBean(clazz);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ObjectName buildObjectName(final Class clazz) throws MalformedObjectNameException {
		return new ObjectName(jrrMBeansPrefix + clazz.getSimpleName());
	}

	public static <T> T makeBeanAndRegisterMBean(final Class<T> clazz)
			throws IntrospectionException, JMException, IllegalAccessException, InstantiationException {
		// Class<T> clazz = (Class) t.getClass();
		final MBeanServer server = JrrUtils.findLocalMBeanServer();
		final ObjectName objectName = buildObjectName(clazz);
		final boolean isMbeanRegisted = server.isRegistered(objectName);
		if (!isMbeanRegisted) {
			log.fine("mbean not registered " + clazz.getSimpleName());
		}
		log.fine("is object name registered " + server.isRegistered(objectName));
		final JrrBeanMethods beanMethods = new JrrBeanMethods(clazz);
		final WeakReference<T> ttWeakReference = cacheProxyBeans.get(clazz);
		if (ttWeakReference != null) {
			// If classloader for clazz is last then return wrapper from cache.
			final T proxy = ttWeakReference.get();
			if (proxy != null) {
				if (!isMbeanRegisted) {
					MBeanFromJavaBean mbean = new MBeanFromJavaBean(beanMethods.getObject());
					beanMethods.setMBean(mbean);
					server.registerMBean(mbean, objectName);
				}
				return proxy;
			}
		}
		boolean isNewBeanImpl = false;
		final T oldBeanValue = (T) beanMethods.getObject();
		if (oldBeanValue == null) {
		} else if (oldBeanValue.getClass() != clazz) {
			ClassLoader oldBeanClassLoader = oldBeanValue.getClass().getClassLoader();
			ClassLoader newBeanClassLoader = clazz.getClassLoader();
			ClassLoader classLoader = oldBeanClassLoader;
			l: {
				while (classLoader != null) {
					if (newBeanClassLoader == classLoader) {
						break l;
					}
					classLoader = classLoader.getParent();
				}
				isNewBeanImpl = true;
			}
		} else {
			throw new IllegalStateException(clazz.getName());
		}
		final T proxy = makeBeanOnly(clazz, beanMethods);
		if (oldBeanValue == null || isNewBeanImpl) {
			beanMethods.setObject(proxy);
			final HashMap allMethods = new HashMap();
			for (final Method method : clazz.getMethods()) {
				if (!Modifier.isStatic(method.getModifiers())) {
					final ArrayList<String> key = buildKey(method);
					allMethods.put(key, method);
				}
			}
			beanMethods.setJavaBeanClass(clazz);
			beanMethods.setMethodsMap(allMethods);
		}
		if (isNewBeanImpl) {
			makeClone2(oldBeanValue, proxy, oldBeanValue.getClass().getSuperclass(), proxy.getClass().getSuperclass());
		}
		// log.fine(server.isRegistered(objectName) + "");
		cacheProxyBeans.put(clazz, new WeakReference<T>(proxy));
		if (isMbeanRegisted) {
			log.fine("unregister mbean");
			JrrUtils.unregisterMBeanQuiet(server, objectName);
		}
		MBeanFromJavaBean mbean = new MBeanFromJavaBean(beanMethods.getObject());
		beanMethods.setMBean(mbean);
		server.registerMBean(mbean, objectName);
		return proxy;
	}

	public final static <T> void makeClone2(final T fromInstance, final T toInstance, Class class1, Class class2)
			throws IllegalAccessException {
		while (class1 != Object.class) {
			if (class1 == class2) {
				JrrClassUtils.makeClone(fromInstance, toInstance, class2);
			} else {
				if (class1.getName().equals(class2.getName())) {
					JrrClassUtils.makeCloneDiffClassloaders(fromInstance, toInstance, class1, class2);
				} else {
					throw new IllegalStateException(
							"Different class names " + class1.getName() + " " + class2.getName());
				}

			}
			class1 = class1.getSuperclass();
			class2 = class2.getSuperclass();
		}
	}

	public static <T> T makeBeanOnly(final Class<T> clazz, final JrrBeanMethods beanMethods)
			throws InstantiationException, IllegalAccessException {

		JrrMethodsInspector jrrMethodsInspector = new JrrMethodsInspector(beanMethods);
		JavassistProxyFactory f = new JavassistProxyFactory(jrrMethodsInspector, false, clazz);
		f.setFilter(DefaultMethodFilter.allPublicMethodFilter);
		return (T) f.createInstance();
	}

	public static Map getBeansStoreMap2() {
		return (Map) JrrUtilities.buildObjectNoEx(SharedObjectsUtils.getGlobalMap(), keyInViewer,
				JrrUtilities.constructorHashMap);
	}

	public static Map<String, Object> getObjectBeanFieldsMap(final Class clazz) {
		return (Map) JrrUtilities.buildObjectNoEx(getBeansStoreMap2(), clazz.getName(),
				JrrUtilities.constructorConcurrentHashMap);
	}

	public static ArrayList<String> buildKey(final Method method) {
		final ArrayList<String> key = new ArrayList();
		key.add(method.getName());
		final Class[] paramsClass = method.getParameterTypes();
		for (final Class class1 : paramsClass) {
			key.add(class1.getName());
		}
		return key;
	}

	public static Object getFieldValue(final Object object, final String fieldName) throws Exception {
		final Field field = JrrClassUtils.findField(object.getClass(), fieldName);
		return field.get(object);
	}

	public static Object getStaticFieldValue(final Class clazz, final String fieldName) throws Exception {
		final Field field = JrrClassUtils.findField(clazz, fieldName);
		return field.get(null);
	}

	public static void setFieldValue(final Object object, final String fieldName, final Object value) throws Exception {
		final Field field = JrrClassUtils.findField(object.getClass(), fieldName);
		field.set(object, value);
	}

	public static void setStaticFieldValue(final Class clazz, final String fieldName, final Object value)
			throws Exception {
		final Field field = JrrClassUtils.findField(clazz, fieldName);
		field.set(null, value);
	}
}

/* 
 * JRemoteRun.sf.net. License:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
 