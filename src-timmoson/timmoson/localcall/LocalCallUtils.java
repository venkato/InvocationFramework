package timmoson.localcall;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javaservice.CallProxyGeneralHandler;
import net.sf.jremoterun.utilities.javassist.JavassistProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.server.ServiceLocator;
import timmoson.server.ServiceSupport;

public class LocalCallUtils {
	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	// public static <T> T makeProxy(final Class<T> clazz) throws Exception {
	// // enhancer.setSuperclass(clazz);
	// final Object service = CallProxy.getProxyObject(clazz);
	// return makeProxy2(clazz, service);
	// }

	public static <T> T makeLocalClient(final Class<T> clazz, String serviceid) throws Exception {
		if (serviceid == null) {
			throw new Exception("service not found " + clazz.getName());
		}

		CallProxyGeneralHandler jrrMethodsInspector = new CallProxyGeneralHandler(clazz) {

			@Override
			public T getService() throws Exception {
				ServiceSupport<T> serviceSupport=  ServiceLocator.getService(clazz);
				return serviceSupport.service;
			}

			@Override
			public Class getProxyServiceClass() {
				return clazz;
			}

			@Override
			public ClassLoader getRemoteCLassLoader() throws Exception {
				return getService().getClass().getClassLoader();
			}

		};
		JavassistProxyFactory f = new JavassistProxyFactory(jrrMethodsInspector, true, clazz);
		return (T) f.createInstance();
	}

}
