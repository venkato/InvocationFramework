		package timmoson.server;

		
import org.apache.log4j.Logger;

		public class GetReponseHandler {
	private static final Logger logger = Logger
			.getLogger(GetReponseHandler.class);
	
	public static GetReponseHandler reponseHandler=new GetReponseHandler();
	
	public ServiceCallServerInvoker defaultServiceCallServerInvoker = new ServiceCallServerInvoker();
	
	public ServiceCallServerInvoker getServiceCallServerInvoker(TcpCallInfoServer callInfo) {
		return defaultServiceCallServerInvoker;
	}
}
