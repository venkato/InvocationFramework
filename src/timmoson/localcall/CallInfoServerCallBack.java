package timmoson.localcall;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;
import timmoson.common.CallBackSession;
import timmoson.common.CallInfoServer;
import timmoson.common.debug.LocalSessionNotifier;

public class CallInfoServerCallBack implements CallInfoServer {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public static LocalSessionNotifier localSessionNotifier;
	
	public LocalSession localSession;
	
	@Override
	public CallBackSession getSession() {
		return localSession;
	}
	
}
