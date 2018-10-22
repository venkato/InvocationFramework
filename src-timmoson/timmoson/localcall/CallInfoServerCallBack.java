package timmoson.localcall;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.common.CallBackSession;
import timmoson.common.CallInfoServer;
import timmoson.common.debug.LocalSessionNotifier;

public class CallInfoServerCallBack implements CallInfoServer {

	public static LocalSessionNotifier localSessionNotifier;
	
	public LocalSession localSession;
	
	@Override
	public CallBackSession getSession() {
		return localSession;
	}
	
}
