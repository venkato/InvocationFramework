package timmoson.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceInfo extends ServiceSupport{

	public String serviceId;

//	public Object service;

//	public NewSessionBuilder newSessionBuilder;
	public ServiceCallServerInvoker serviceCallServerInvoker = new ServiceCallServerInvoker();

	public ServiceInfo(String serviceId, Object service) {
		this.serviceId = serviceId;
		this.service = service;
	}

}
