package timmoson.server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.CallBackSession;
import timmoson.common.CallInfoServer;
import timmoson.common.TcpCallCommon;

public class TcpCallInfoServer extends TcpCallCommon implements CallInfoServer {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	// remove recently
//	public volatile Method method;
	// Object serviceId;
	public volatile ServiceSupport serviceSupport;


//	public ServiceCallServerInvoker serviceCallServerInvoker;
//	public TcpServiceObject sessionService;
//	String requestId;
	@Override
	public CallBackSession getSession() {
		return tcpSession;
	}
	
}
