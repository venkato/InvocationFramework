package timmoson.localcall;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.CallBackSession;

public class LocalSession extends CallBackSession {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	@Override
	public <T> T makeClient(Class<T> class1, String serviceId) throws Exception {
//		ServiceInfo serviceInfo = ServiceLocator.services.get(serviceId);
		T t;
//		if (class1.isInstance(serviceInfo.service)) {
//			t = (T) serviceInfo.service;
//		} else {
			t = LocalCallUtils.makeLocalClient(class1, serviceId);
//		}
		return t;
	}
}
