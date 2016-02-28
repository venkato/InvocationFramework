package timmoson.testservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.reflect.Reflection;

public class CallbackService {

	private static final Log log = LogFactory.getLog(Reflection
			.getCallerClass(1));

	public void callback() {
		log.info("callback ok");
	}
}
