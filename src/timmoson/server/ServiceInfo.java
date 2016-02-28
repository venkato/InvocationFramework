package timmoson.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

public class ServiceInfo extends ServiceSupport{
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public String serviceId;

//	public Object service;

//	public NewSessionBuilder newSessionBuilder;
	public ServiceCallServerInvoker serviceCallServerInvoker = new ServiceCallServerInvoker();

	public ServiceInfo(String serviceId, Object service) {
		this.serviceId = serviceId;
		this.service = service;
	}

}
