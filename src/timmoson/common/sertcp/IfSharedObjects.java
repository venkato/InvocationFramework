package timmoson.common.sertcp;

import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.Reflection;

import java.util.HashMap;

public class IfSharedObjects {
	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public static String IfSharedObjectsS = "timmoson.IfSharedObjects";
	public static String callInfoS = "timmoson.callInfo";
	public static String servicesS = "timmoson.sevices";

	public static HashMap<String, Object> ifObjects = (HashMap) JrrUtilities
			.buildObjectNoEx(SharedObjectsUtils.getGlobalMap(),
					IfSharedObjectsS, JrrUtilities.constructorHashMap);

	public static HashMap<String, Object> services = (HashMap) JrrUtilities
			.buildObjectNoEx(ifObjects, servicesS,
					JrrUtilities.constructorHashMap);

	// <CallInfoServer>
	public static ThreadLocal getCallInfo() {
		ThreadLocal threadLocal = (ThreadLocal) ifObjects.get(callInfoS);
		if (threadLocal == null) {
			threadLocal = new ThreadLocal();
			ifObjects.put(callInfoS, threadLocal);
		}
		return threadLocal;
	}

}
