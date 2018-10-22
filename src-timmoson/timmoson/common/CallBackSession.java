package timmoson.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class CallBackSession {
//	private static final Log log = LogFactory.getLog(Reflection
//			.getCallerClass(1));

	public Object customProperties;

	public <T> T makeClient(Class<T> class1) throws Exception {
		return makeClient(class1, class1.getName());
	}

	public abstract <T> T makeClient(Class<T> class1, String serviceId)
			throws Exception;
}
