package timmoson.testservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CallbackService {

	private static final Log log = LogFactory.getLog(CallbackService.class);

	public void callback() {
		log.info("callback ok");
	}
}
