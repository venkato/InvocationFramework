		package timmoson.server;

		import groovy.transform.CompileStatic;
		import org.apache.log4j.Logger;

		@CompileStatic
		public class ServiceSupport<T> {
	private static final Logger logger = Logger.getLogger(ServiceSupport.class);
	
	public T service;
	
//	public ServiceCallServerInvoker serviceCallServerInvoker=new ServiceCallServerInvoker();
	
}
