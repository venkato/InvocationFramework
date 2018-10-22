package timmoson.server

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.sertcp.IfSharedObjects;

@CompileStatic
public class ServiceLocator {
	private static final Log log = LogFactory.getLog(JrrClassUtils.getCurrentClass());

	public static <T> ServiceSupport<T> getService(Class<T> clazz) throws Exception {
		ServiceSupport<T> service = getService(clazz.getName());
		return service;
	}

	public static ServiceSupport getService(String serviceId) throws Exception {
		Object service = IfSharedObjects.services.get(serviceId);
		if (service == null) {
			throw new Exception(serviceId + " not found");
		}
		ServiceSupport serviceSupport = new ServiceSupport();
		serviceSupport.service = service;
		return serviceSupport;

	}

	public static <T> ServiceSupport<T> getServiceRE(Class<T> clazz) {
		ServiceSupport<T> service = getServiceRE(clazz.getName());
		return service;
	}

	public static ServiceSupport getServiceRE(String serviceId) {
		Object service = IfSharedObjects.services.get(serviceId);
		if (service == null) {
			throw new RuntimeException(serviceId + " not found");
		}
		ServiceSupport serviceSupport = new ServiceSupport();
		serviceSupport.service = service;
		return serviceSupport;
	}

	public static void regNewService(Object service) {
		regNewService((Class) service.getClass(), service);
	}

	public static <T> void regNewService(Class<T> referenceId, T service) {
		regNewService(referenceId.getName(), service);
	}

	// public static void classLoaderCheck() throws Exception {
	// Collection<ServiceInfo> values = services.values();
	// for (ServiceInfo serviceInfo : values) {
	// // if(serviceInfo.service==TestService.class) {
	// // continue;
	// // }
	// ClassLoader serviceClassLoader = serviceInfo.service.getClass()
	// .getClassLoader();
	// if (serviceClassLoader == null) {
	// throw new Exception(serviceInfo.serviceId + " "
	// + serviceInfo.service);
	// }
	// Class<?> otherClass = serviceClassLoader
	// .loadClass(ServiceLocator.class.getName());
	// if (otherClass != ServiceLocator.class) {
	// throw new Exception(
	// "Low functionality due to classloader configuration");
	// }
	// }
	// }

	public static void regNewService(String serviceId, Object service) {
		// if (serviceInfo.service.getClass().getClassLoader() == null) {
		// log.info(serviceInfo.serviceId, new Exception());
		// }
		Object put = IfSharedObjects.services.put(serviceId, service);
		if (put != null) {
			log.error("exits service before " + serviceId + " " + put.getClass().getName(), new Exception());
		}
	}

}
